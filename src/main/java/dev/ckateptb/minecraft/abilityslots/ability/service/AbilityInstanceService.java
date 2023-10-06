package dev.ckateptb.minecraft.abilityslots.ability.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.service.config.LagPreventConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static reactor.core.scheduler.Schedulers.DEFAULT_POOL_SIZE;

@Component
@CustomLog
@RequiredArgsConstructor
public class AbilityInstanceService implements Listener {
    private Duration block;
    private Duration lagThreshold;
    private boolean lagAlert;
    private final Scheduler singleScheduler = Schedulers.newSingle("abilities", false);
    private final Scheduler parallelScheduler = Schedulers.newParallel("abilities", DEFAULT_POOL_SIZE, false);
    private final Scheduler timeoutScheduler = Schedulers.newParallel("timeout", DEFAULT_POOL_SIZE, true);
    private final Set<Ability> abilities = Collections.synchronizedSet(ConcurrentHashMap.newKeySet());

    private final AbilitySlotsConfig config;
    private final AbilityCollisionService collisionService;

    public void register(Ability ability) {
        this.abilities.add(ability);
    }

    @Schedule(initialDelay = 0, fixedRate = 1, async = true)
    private synchronized void process() {
        if (this.abilities.isEmpty()) return;
        this.collisionService.findCollided(this.abilities)
                .subscribe(this::destroy);
        this.tickAbilities();
    }

    private void tickAbilities() {
        try {
            boolean parallel = this.block == null;
            Mono<Void> publisher = Flux.fromIterable(this.abilities)
                    .filter(ability -> !ability.isLocked())
                    .flatMap(ability -> this.tick(ability, parallel))
                    .then();
            if(!parallel) publisher.block(this.block);
            else publisher.subscribe();
        } catch (Throwable throwable) {
            this.abilities.forEach(this::destroy);
            this.abilities.clear();
            if (throwable.getMessage().contains("Timeout on blocking read")) {
                if (this.lagAlert) {
                    log.warn("Abilities processing timed out all abilities was destroyed to prevent lags.");
                }
            } else {
                log.error("An error occurred while processing abilities and all abilities was force destroyed.", throwable);
            }
        }
    }

    private Mono<Ability> tick(Ability ability, boolean parallel) {
        return Mono.just(ability)
                .publishOn(parallel ? this.parallelScheduler : this.singleScheduler)
                .doOnNext(value -> ability.setLocked(true))
                .map(Ability::tick)
                .timeout(this.lagThreshold, this.timeoutScheduler)
                .onErrorReturn(throwable -> {
                    IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                    String name = declaration.getName();
                    String author = declaration.getAuthor();
                    if (throwable instanceof TimeoutException) {
                        if (this.lagAlert) {
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
                .doOnNext(value -> ability.setLocked(false))
                .filter(status -> status == AbilityTickStatus.DESTROY)
                .map(status -> ability)
                .doOnNext(this::destroy);
    }

    public void destroy(Ability... destroyed) {
        for (Ability ability : destroyed) {
            ability.setLocked(true);
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

    @EventHandler(priority = EventPriority.LOW)
    private void on(AbilitySlotsReloadEvent event) {
        this.destroy(this.abilities.toArray(Ability[]::new));
        LagPreventConfig lagPrevent = this.config.getGlobal().getLagPrevent();
        long threshold = lagPrevent.getDropAllThreshold();
        this.lagThreshold = Duration.of(lagPrevent.getLagThreshold(), ChronoUnit.MILLIS);
        this.lagAlert = lagPrevent.isAlertDestroyed();
        if (threshold > 0) {
            this.block = Duration.of(threshold, ChronoUnit.MILLIS);
        } else this.block = null;
    }
}
