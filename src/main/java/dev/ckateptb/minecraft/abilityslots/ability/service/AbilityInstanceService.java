package dev.ckateptb.minecraft.abilityslots.ability.service;

import com.google.common.collect.Sets;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.atom.chain.AtomChain;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Component
@CustomLog
public class AbilityInstanceService {
    private final Set<Ability> abilities = new HashSet<>();

    public void register(Ability ability) {
        this.abilities.add(ability);
    }

    @Schedule(initialDelay = 20, fixedRate = 1, async = true)
    protected synchronized void process() {
        int parallelism = this.abilities.size();
        if (parallelism == 0) return;
        Set<Ability> destroyed = this.processActive(parallelism);
        this.processDestroy(destroyed);
    }

    private Set<Ability> processActive(int parallelism) {
        Set<Ability> destroyed = new HashSet<>();
        Flux.fromIterable(this.abilities)
                .parallel(parallelism)
                .runOn(Schedulers.boundedElastic())
                .flatMap(ability -> Mono.just(ability)
                        .publishOn(Schedulers.parallel())
                        .map(Ability::tick)
                        .timeout(Duration.of(50, ChronoUnit.MILLIS), Schedulers.parallel())
                        .onErrorReturn(throwable -> {
                            IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                            String name = declaration.getName();
                            String author = declaration.getAuthor();
                            if (throwable instanceof TimeoutException) {
                                log.warn("{} ability processing timed out and was destroyed to prevent lags." +
                                        " Contact the author {}.", name, author);
                            } else {
                                log.error("There was an error processing ability {} and has been called back." +
                                        " Contact the author {}.", name, author);
                                log.error(throwable.getMessage(), throwable);
                            }
                            return true;
                        }, AbilityTickStatus.DESTROY)
                        .doOnError(throwable -> log.error("do on error"))
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

    public Set<Ability> instances(AbilityUser user) {
        return this.abilities.stream()
                .filter(ability -> Objects.equals(user, ability.getUser()))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Ability> instances() {
        return Collections.unmodifiableSet(this.abilities);
    }
}
