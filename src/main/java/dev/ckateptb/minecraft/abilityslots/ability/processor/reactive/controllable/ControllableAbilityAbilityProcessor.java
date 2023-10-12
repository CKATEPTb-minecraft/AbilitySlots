package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.controllable;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.AbstractReactiveAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.controllable.config.ControllableProcessingConfig;
import lombok.CustomLog;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import static reactor.core.scheduler.Schedulers.DEFAULT_POOL_SIZE;

@CustomLog
public class ControllableAbilityAbilityProcessor extends AbstractReactiveAbilityProcessor {
    private final Duration abilityThreshold;
    private final boolean alertDestroyed;
    private final Duration blockDuration;
    private final Scheduler timeout = Schedulers.newParallel("timeout", DEFAULT_POOL_SIZE, true);
    private final Scheduler scheduler;

    public ControllableAbilityAbilityProcessor(Scheduler scheduler, ControllableProcessingConfig config) {
        this.scheduler = scheduler;
        long aThreshold = config.getAbilityThreshold();
        if (aThreshold > 0) {
            this.abilityThreshold = Duration.ofMillis(aThreshold);
        } else this.abilityThreshold = null;
        this.alertDestroyed = config.isAlertDestroyed();
        long tickThreshold = config.getTickThreshold();
        if (tickThreshold > 0) {
            this.blockDuration = Duration.ofMillis(tickThreshold);
        } else this.blockDuration = null;
    }

    @Override
    public void process(Collection<Ability> abilities) {
        try {
            Flux<Ability> flux = Flux.fromIterable(abilities)
                    .filter(ability -> !ability.isLocked())
                    .flatMap(ability -> {
                                Mono<AbilityTickStatus> statusMono = Mono.just(ability)
                                        .publishOn(this.scheduler)
                                        .doOnNext(value -> ability.setLocked(true))
                                        .map(Ability::tick);
                                if (this.abilityThreshold != null) {
                                    statusMono = statusMono.timeout(this.abilityThreshold, this.timeout);
                                }
                                return statusMono
                                        .onErrorReturn(throwable -> {
                                            IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                                            String name = declaration.getName();
                                            String author = declaration.getAuthor();
                                            if (throwable instanceof TimeoutException) {
                                                if (this.alertDestroyed) {
                                                    log.warn("{} ability processing timed out and was destroyed to" +
                                                            " prevent lags. Contact the author {}.", name, author);
                                                }
                                            } else {
                                                log.error(MessageFormatter.arrayFormat(
                                                        "There was an error processing ability {} and has" +
                                                                " been called back. Contact the author {}.",
                                                        new Object[]{name, author}
                                                ).getMessage(), throwable);
                                            }
                                            return true;
                                        }, AbilityTickStatus.DESTROY)
                                        .doOnNext(value -> ability.setLocked(false))
                                        .filter(status -> status == AbilityTickStatus.DESTROY)
                                        .map(status -> ability)
                                        .doOnNext(Ability::destroy);
                            }
                    );
            if (this.blockDuration != null) {
                flux.then().block(this.blockDuration);
            } else flux.subscribe();
        } catch (Throwable throwable) {
            abilities.forEach(Ability::destroy);
            if (throwable.getMessage().contains("Timeout on blocking read")) {
                if (this.alertDestroyed) {
                    log.warn("Abilities processing timed out all abilities was destroyed to prevent lags.");
                }
            } else {
                log.error("An error occurred while processing abilities and all abilities was force destroyed.", throwable);
            }
        }
    }
}
