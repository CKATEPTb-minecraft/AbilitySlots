package dev.ckateptb.minecraft.abilityslots.ability.lifecycle.process;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.AbstractAbilityLifecycle;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.config.LifecycleConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.atom.Atom;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
@CustomLog
public class AbilityProcessLifecycle extends AbstractAbilityLifecycle<Ability> {
    private final AbilityUserService userService;
    private final AbilityCollisionService collisionService;
    private final AbilitySlotsConfig config;
    private final Server server;

    public AbilityProcessLifecycle(AbilityUserService userService, AbilityCollisionService collisionService, AbilitySlotsConfig config) {
        this.userService = userService;
        this.collisionService = collisionService;
        this.config = config;
        this.server = Bukkit.getServer();
    }

    @PostConstruct
    public void process() {
        this.flux
                .filter(Ability::isAccessible)
                .doOnNext(Ability::lock)
                .flatMap(ability -> {
                            long threshold = this.config.getGlobal().getPerformance().getThreshold();
                            Mono<AbilityTickStatus> status = Mono.just(ability)
                                    .publishOn(this.getScheduler())
                                    .map(Ability::tick);
                            if (threshold > 0) {
                                status = status.timeout(Duration.ofMillis(Math.max(50, threshold)));
                            }
                            return status
                                    .onErrorReturn(throwable -> {
                                        IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                                        String name = declaration.getName();
                                        String author = declaration.getAuthor();
                                        if (throwable instanceof TimeoutException) {
                                            log.warn("{} ability processing timed out and was destroyed to" +
                                                    " prevent lags. Contact the author {}.", name, author);
                                        } else {
                                            log.error(MessageFormatter.arrayFormat(
                                                    "There was an error processing ability {} and has" +
                                                            " been called back. Contact the author {}.",
                                                    new Object[]{declaration.getName(), declaration.getAuthor()}
                                            ).getMessage(), throwable);
                                        }
                                        return true;
                                    }, AbilityTickStatus.DESTROY)
                                    .map(result -> Tuples.of(ability, result));
                        }
                )
                .subscribe(objects -> {
                    Ability ability = objects.getT1();
                    AbilityTickStatus status = objects.getT2();
                    if (status == AbilityTickStatus.DESTROY) {
                        ability.destroy();
                    }
                    ability.unlock();
                });
    }

    @Schedule(fixedRate = 1, initialDelay = 0)
    public void tick() {
        List<Ability> list = this.userService.getAbilityUsers().flatMap(AbilityUser::getAbilityInstances).toList();
        this.collisionService.findCollided(list).subscribe(Ability::destroy);
        list.forEach(this::emit);
    }

    private Scheduler getScheduler() {
        LifecycleConfig.Ticking ticking = this.config.getGlobal().getPerformance().getTicking();
        return switch (ticking) {
            case SYNC -> Atom.syncScheduler();
            case ASYNC -> Schedulers.boundedElastic();
            case SMART -> {
                if (this.server.getTPS()[0] > 18) {
                    yield Atom.syncScheduler();
                } else {
                    yield Schedulers.boundedElastic();
                }
            }
        };
    }
}
