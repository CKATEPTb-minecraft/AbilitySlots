package dev.ckateptb.minecraft.abilityslots.ability.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Component
@CustomLog
public class AbilityInstanceService {
    private final Sinks.Many<AbilityWrapper> abilities = Sinks.many().replay().all();

    public void register(Ability ability) {
        this.abilities.tryEmitNext(new AbilityWrapper(ability));
    }

    @Schedule(initialDelay = 1, fixedRate = 1)
    public void process() {
        this.abilities.asFlux()
                .filter(wrapper -> !wrapper.destroyed)
                .subscribeOn(Schedulers.parallel())
                .subscribe(wrapper -> {
                    Ability ability = wrapper.ability;
                    IAbilityDeclaration<Ability> abilityDeclaration = ability.getAbilityDeclaration();
                    String name = abilityDeclaration.getName();
                    String author = abilityDeclaration.getAuthor();
                    // Если обработка способности занимает 5 и более тиков,
                    // то способность считается зависшей и ее необходимо ликвидировать.
                    if (wrapper.schedule != null) {
                        log.warn("Ability {} did not finish processing in the allotted time", name);
                        if (++wrapper.noTickCount >= 5) {
                            wrapper.schedule.dispose();
                            wrapper.destroyed = true;
                            log.warn("Processing ability {} is taking too long and has been called back. Contact the author {}.",
                                    name, author);
                        }
                    } else {
                        wrapper.schedule = Schedulers.single().schedule(() -> {
                            try {
                                wrapper.destroyed = ability.tick() == AbilityTickStatus.DESTROY;
                            } catch (Throwable throwable) {
                                log.warn("There was an error processing ability {} and has been called back. Contact the author {}",
                                        name, author);
                            }
                            wrapper.noTickCount = 0;
                            wrapper.schedule = null;
                        });
                    }
                });
    }

    public Flux<Ability> instances(AbilityUser user) {
        return this.abilities.asFlux()
                .filter(wrapper -> !wrapper.destroyed)
                .map(wrapper -> wrapper.ability)
                .filter(ability -> ability.getUser() == user);
    }

    @RequiredArgsConstructor
    private static class AbilityWrapper {
        private final Ability ability;
        private boolean destroyed;
        private int noTickCount;
        private Disposable schedule;

        @Override
        public boolean equals(Object other) {
            return ability.equals(other);
        }

        @Override
        public int hashCode() {
            return ability.hashCode();
        }
    }
}
