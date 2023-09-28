package dev.ckateptb.minecraft.abilityslots.ability.service;

import com.google.common.collect.Sets;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.enums.AbilityCollisionResult;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.service.config.LagPreventConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.atom.chain.AtomChain;
import dev.ckateptb.minecraft.colliders.Collider;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Component
@CustomLog
@RequiredArgsConstructor
public class AbilityInstanceService {
    private final Set<Ability> abilities = new HashSet<>();
    private final AbilitySlotsConfig config;

    public void register(Ability ability) {
        this.abilities.add(ability);
    }

    @Schedule(initialDelay = 20, fixedRate = 1, async = true)
    private synchronized void process() {
        int parallelism = this.abilities.size();
        if (parallelism == 0) return;
        Set<Ability> destroyed = this.processActive(parallelism);
        this.processDestroy(destroyed);
    }

    private Set<Ability> processActive(int parallelism) {
        LagPreventConfig lagPrevent = this.config.getGlobal().getLagPrevent();
        Set<Ability> destroyed = Collections.synchronizedSet(new HashSet<>());
        Flux.fromIterable(this.abilities)
                .parallel(parallelism)
                .runOn(Schedulers.boundedElastic())
                .sorted((o1, o2) -> {
                    // Ability Collision - Start
                    if (o1 instanceof CollidableAbility first && o2 instanceof CollidableAbility second) {
                        Collection<Collider> firstColliders = first.getColliders();
                        if (firstColliders == null) return 0;
                        Collection<Collider> secondColliders = second.getColliders();
                        if (secondColliders == null) return 0;
                        if (firstColliders.isEmpty() || secondColliders.isEmpty()) return 0;
                        if (!first.getWorld().getUID().equals(second.getWorld().getUID())) return 0;
                        boolean firstDestructSecond = first.getCollisionDeclaration().isDestruct(second);
                        boolean secondDestructFirst = second.getCollisionDeclaration().isDestruct(first);
                        if (firstDestructSecond || secondDestructFirst) {
                            for (Collider collider : firstColliders) {
                                if (collider == null) continue;
                                for (Collider other : secondColliders) {
                                    if (other == null) continue;
                                    ;
                                    if (collider.intersects(other) || other.intersects(collider)) {
                                        if (firstDestructSecond && second.onCollide(other, first, collider) == AbilityCollisionResult.DESTROY) {
                                            destroyed.add(second);
                                        }
                                        if (secondDestructFirst && first.onCollide(collider, second, other) == AbilityCollisionResult.DESTROY) {
                                            destroyed.add(first);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Ability Collision - End
                    return 0;
                })
                .parallel(parallelism)
                .runOn(Schedulers.boundedElastic())
                .flatMap(ability -> Mono.just(ability)
                        .publishOn(Schedulers.parallel())
                        .filter(value -> !destroyed.contains(value))
                        .map(Ability::tick)
                        .timeout(Duration.of(lagPrevent.getLagThreshold(), ChronoUnit.MILLIS), Schedulers.parallel())
                        .onErrorReturn(throwable -> {
                            IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                            String name = declaration.getName();
                            String author = declaration.getAuthor();
                            if (throwable instanceof TimeoutException) {
                                if (lagPrevent.isAlertDestroyed()) {
                                    log.warn("{} ability processing timed out and was destroyed to prevent lags." +
                                            " Contact the author {}.", name, author);
                                }
                            } else {
                                log.error("There was an error processing ability {} and has been called back." +
                                        " Contact the author {}.", name, author);
                                log.error(throwable.getMessage(), throwable);
                            }
                            return true;
                        }, AbilityTickStatus.DESTROY)
                        .doOnNext(status -> {
                            if (status == AbilityTickStatus.DESTROY) {
                                destroyed.add(ability);
                            }
                        })
                )
                .sequential()
                .count()
                .block(Duration.of(2, ChronoUnit.SECONDS));
        return destroyed;
    }

    public void destroy(Ability ability) {
        AtomChain.sync(ability).promise(destroyed -> {
            if (this.abilities.contains(ability)) {
                this.processDestroy(Sets.newHashSet(destroyed));
            }
        });
    }

    private void processDestroy(Set<Ability> destroyed) {
        int size = destroyed.size();
        if (size == 0) return;
        destroyed.removeIf(ability -> !this.abilities.contains(ability));
        this.abilities.removeAll(destroyed);
        Flux.fromIterable(destroyed)
                .parallel(size)
                .runOn(Schedulers.boundedElastic())
                .flatMap(ability -> Mono.just(ability)
                        .publishOn(Schedulers.parallel())
                        .doOnNext(value -> value.destroy(null))
                ).subscribe();
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
