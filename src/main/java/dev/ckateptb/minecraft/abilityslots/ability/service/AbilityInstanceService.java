package dev.ckateptb.minecraft.abilityslots.ability.service;

import com.google.common.collect.Sets;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.ICollisionDeclaration;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
        Set<Ability> destroyed = new HashSet<>();
        Flux.fromIterable(this.abilities)
                .parallel(parallelism)
                .runOn(Schedulers.boundedElastic())
                .flatMap(ability -> Mono.just(ability)
                        .publishOn(Schedulers.parallel())
                        .map(current -> {
                            // Ability Collision - Start
                            if (current instanceof CollidableAbility collidableAbility) {
                                Collection<Collider> colliders = collidableAbility.getColliders();
                                if (!colliders.isEmpty()) {
                                    ICollisionDeclaration declaration = collidableAbility.getCollisionDeclaration();
                                    Collection<Class<? extends CollidableAbility>> destructible = declaration.getDestructible();
                                    for (Class<? extends CollidableAbility> otherClass : destructible) {
                                        for (CollidableAbility other : this.instances(otherClass)
                                                .map(other -> (CollidableAbility) other)
                                                .filter(other -> !other.getColliders().isEmpty())
                                                .toList()) {
                                            boolean otherDestructCurrent = other.getCollisionDeclaration().isDestruct(collidableAbility);
                                            for (Collider otherCollider : other.getColliders()) {
                                                for (Collider collider : colliders) {
                                                    if (collider.intersects(otherCollider)) {
                                                        if (other.onCollide(otherCollider, collidableAbility, collider) == AbilityCollisionResult.DESTROY) {
                                                            destroyed.add(ability);
                                                        }
                                                        if (otherDestructCurrent) {
                                                            if (collidableAbility.onCollide(collider, other, otherCollider) == AbilityCollisionResult.DESTROY) {
                                                                return AbilityTickStatus.DESTROY;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            // Ability Collision - End
                            return current.tick();
                        })
                        .timeout(Duration.of(lagPrevent.getLagThreshold(), ChronoUnit.MILLIS), Schedulers.parallel())
                        .onErrorReturn(throwable -> {
                            IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                            String name = declaration.getName();
                            String author = declaration.getAuthor();
                            if (throwable instanceof TimeoutException) {
                                if(lagPrevent.isAlertDestroyed()) {
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
