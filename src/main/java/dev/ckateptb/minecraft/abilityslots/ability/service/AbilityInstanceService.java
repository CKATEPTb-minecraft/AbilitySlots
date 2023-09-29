package dev.ckateptb.minecraft.abilityslots.ability.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.service.config.LagPreventConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Component
@CustomLog
@RequiredArgsConstructor
public class AbilityInstanceService {
    private final Scheduler abilityScheduler = Schedulers.newSingle("abilities", true);
    private final Scheduler timeoutScheduler = Schedulers.newSingle("timeout", true);
    private final Set<Ability> abilities = Collections.synchronizedSet(ConcurrentHashMap.newKeySet());

    private final AbilitySlotsConfig config;
    private final AbilityCollisionService collisionService;

    public void register(Ability ability) {
        this.abilities.add(ability);
    }

    @Schedule(initialDelay = 0, fixedRate = 1, async = true)
    private synchronized void process() {
        if (this.abilities.isEmpty()) return;
        this.tickAbilities();
        this.collisionService.findCollided(this.abilities)
                .subscribe(this::destroy);
    }

    private void tickAbilities() {
        LagPreventConfig lagPrevent = this.config.getGlobal().getLagPrevent();
        try {
            Flux.fromIterable(this.abilities)
                    .flatMap(ability -> Mono.just(ability)
                            .publishOn(this.abilityScheduler)
                            .map(Ability::tick)
                            .timeout(Duration.of(lagPrevent.getLagThreshold(), ChronoUnit.MILLIS), this.timeoutScheduler)
                            .onErrorReturn(throwable -> {
                                IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                                String name = declaration.getName();
                                String author = declaration.getAuthor();
                                if (throwable instanceof TimeoutException) {
                                    if (lagPrevent.isAlertDestroyed()) {
                                        log.warn("{} ability processing timed out and was destroyed to" +
                                                " prevent lags. Contact the author {}.", name, author);
                                    }
                                } else {
                                    log.error(MessageFormatter.arrayFormat(
                                            "There was an error processing ability {} and has" +
                                                    " been called back. Contact the author {}.",
                                            new Object[]{declaration.getName(), declaration.getAuthor()}
                                    ).getMessage(), throwable);
                                }
                                return true;
                            }, AbilityTickStatus.DESTROY)
                            .filter(status -> status == AbilityTickStatus.DESTROY)
                            .map(status -> ability)
                            .doOnNext(this::destroy)
                    )
                    .then()
                    .block(Duration.of(lagPrevent.getDropAllThreshold(), ChronoUnit.MILLIS));
        } catch (Throwable throwable) {
            this.abilities.forEach(this::destroy);
            this.abilities.clear();
            if (throwable.getMessage().contains("Timeout on blocking read")) {
                log.warn("Abilities processing timed out all abilities was destroyed to prevent lags.");
            } else {
                log.error("An error occurred while processing abilities and all abilities was force destroyed.", throwable);
            }
        }
    }

    public void destroy(Ability... destroyed) {
        for (Ability ability : destroyed) {
            if (this.abilities.remove(ability)) {
                try {
                    ability.destroy(null);
                } catch (Throwable exception) {
                    IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                    log.error(MessageFormatter.arrayFormat(
                            "There was an error on ability {} was destroyed. Contact the author {}.",
                            new Object[]{declaration.getName(), declaration.getAuthor()}
                    ).getMessage(), exception);
                }
            }
        }
    }

    public synchronized Stream<Ability> instances(AbilityUser user) {
        return this.abilities.stream()
                .filter(ability -> Objects.equals(user, ability.getUser()));
    }

    public synchronized Stream<Ability> instances(Class<? extends Ability> type) {
        return this.abilities.stream().filter(type::isInstance);
    }

    public synchronized Stream<Ability> instances() {
        return this.abilities.stream();
    }
}
