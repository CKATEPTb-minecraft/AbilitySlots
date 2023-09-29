package dev.ckateptb.minecraft.abilityslots.ability.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.service.config.LagPreventConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.atom.chain.AtomChain;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

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
    private final Set<Ability> abilities = Collections.synchronizedSet(ConcurrentHashMap.newKeySet());
    private final AbilitySlotsConfig config;
    private final Scheduler scheduler = Schedulers.newSingle("abilities");
    private boolean locked;
    public void register(Ability ability) {
        this.abilities.add(ability);
    }

    @Schedule(initialDelay = 0, fixedRate = 1)
    private synchronized void process() {
        if (this.locked || this.abilities.isEmpty()) return;
        try {
            this.tickAbilities();
        } catch (Exception exception) {
            this.abilities.forEach(this::destroy);
            this.abilities.clear();
            log.error("An error occurred while processing abilities and all abilities was force destroyed.", exception);
        }
    }

    public void tickAbilities() {
        this.locked = true;
        LagPreventConfig lagPrevent = this.config.getGlobal().getLagPrevent();
        Flux.fromIterable(this.abilities)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(ability -> Mono.just(ability)
                        .publishOn(this.scheduler)
                        .map(Ability::tick)
                        .timeout(Duration.of(lagPrevent.getLagThreshold(), ChronoUnit.MILLIS), this.scheduler)
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
                        .zipWith(Mono.just(ability))
                )
                .filter(tuple2 -> tuple2.getT1() == AbilityTickStatus.DESTROY)
                .map(Tuple2::getT2)
                .doOnNext(this::destroy)
                .doOnComplete(() -> this.locked = false)
                .subscribe();
    }

    public void destroy(Ability ability) {
        AtomChain.sync(ability).promise(destroyed -> {
            if (this.abilities.contains(ability)) {
                this.processDestroy(destroyed);
            }
        });
    }

    private void processDestroy(Ability... destroyed) {
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
