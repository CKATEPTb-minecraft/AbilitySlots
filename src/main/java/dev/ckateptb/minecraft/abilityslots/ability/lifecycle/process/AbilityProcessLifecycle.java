package dev.ckateptb.minecraft.abilityslots.ability.lifecycle.process;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.AbstractAbilityLifecycle;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
@CustomLog
@RequiredArgsConstructor
public class AbilityProcessLifecycle extends AbstractAbilityLifecycle<Ability> {
    private final AbilityUserService userService;
    private final AbilityCollisionService collisionService;

    @PostConstruct
    public void process() {
        this.flux
                .filter(ability -> !ability.isLocked())
                .doOnNext(ability -> ability.setLocked(true))
                .flatMap(ability -> Mono.just(ability)
                        .map(Ability::tick)
                        .timeout(Duration.ofMillis(50))
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
                        .map(status -> Tuples.of(ability, status))
                )
                .doOnNext(objects -> objects.getT1().setLocked(false))
                .subscribe(objects -> {
                    Ability ability = objects.getT1();
                    AbilityTickStatus status = objects.getT2();
                    if (status == AbilityTickStatus.DESTROY) {
                        ability.destroy();
                    }
                });
    }

    @Schedule(fixedRate = 1, initialDelay = 0, async = true)
    public void tickAsync() {
        if (Bukkit.getServer().getTPS()[0] >= 18) return;
        this.tickAll();
    }

    @Schedule(fixedRate = 1, initialDelay = 0)
    public void tickSync() {
        if (Bukkit.getServer().getTPS()[0] < 18) return;
        this.tickAll();
    }

    private void tickAll() {
        List<Ability> list = this.userService.getAbilityUsers().flatMap(AbilityUser::getAbilityInstances).toList();
        this.collisionService.findCollided(list).subscribe(Ability::destroy);
        list.forEach(this::emit);
    }
}
