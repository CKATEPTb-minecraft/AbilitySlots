package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.parallel;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.AbstractReactiveAbilityProcessor;
import lombok.CustomLog;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;

import static reactor.core.scheduler.Schedulers.DEFAULT_POOL_SIZE;

@CustomLog
public class ParallelAbilityAbilityProcessor extends AbstractReactiveAbilityProcessor {
    private final Scheduler scheduler = Schedulers.newParallel("abilities-p",
            DEFAULT_POOL_SIZE, false);

    @Override
    public void process(Collection<Ability> abilities) {
        Flux.fromIterable(abilities)
                .parallel()
                .runOn(this.scheduler)
                .filter(ability -> !ability.isLocked())
                .doOnNext(ability -> {
                    ability.setLocked(true);
                    IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                    String name = declaration.getName();
                    String author = declaration.getAuthor();
                    try {
                        if (ability.tick() == AbilityTickStatus.CONTINUE) {
                            ability.setLocked(false);
                            return;
                        }
                    } catch (Throwable throwable) {
                        log.error(MessageFormatter.arrayFormat(
                                "There was an error processing ability {} and has" +
                                        " been called back. Contact the author {}.",
                                new Object[]{name, author}
                        ).getMessage(), throwable);
                    }
                    ability.destroy();
                })
                .subscribe();
    }
}
