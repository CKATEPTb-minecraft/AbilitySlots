package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.emit;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.ReactiveAbilityProcessor;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collection;

@CustomLog
@RequiredArgsConstructor
public class EmitAbilityProcessor implements ReactiveAbilityProcessor {
    private final Sinks.Many<Tuple2<Ability, Boolean>> sinks = Sinks.many().unicast().onBackpressureBuffer();
    private final Flux<Tuple2<Ability, Boolean>> flux = this.sinks.asFlux();

    private final Scheduler scheduler;
    private boolean initialized;

    public void initialize() {
        this.initialized = true;
        this.flux
                .publishOn(this.scheduler)
                .subscribe(objects -> {
                    Ability ability = objects.getT1();
                    if (ability.isLocked()) return;
                    Boolean destroy = objects.getT2();
                    IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                    String name = declaration.getName();
                    String author = declaration.getAuthor();
                    ability.setLocked(true);
                    if (destroy) {
                        try {
                            ability.destroy(null);
                        } catch (Throwable exception) {
                            log.error(MessageFormatter.arrayFormat(
                                    "There was an error on ability {} was destroyed. " +
                                            "Contact the author {}.", new Object[]{name, author}
                            ).getMessage(), exception);
                        }
                    } else {
                        AbilityTickStatus status = AbilityTickStatus.DESTROY;
                        try {
                            status = ability.tick();
                        } catch (Throwable throwable) {
                            log.error(MessageFormatter.arrayFormat(
                                    "There was an error processing ability {} and has" +
                                            " been called back. Contact the author {}.",
                                    new Object[]{name, author}
                            ).getMessage(), throwable);
                        }
                        ability.setLocked(false);
                        if (status == AbilityTickStatus.DESTROY) {
                            this.destroy(ability);
                        }
                    }
                });
    }

    @Override
    public void destroy(Ability... destroyed) {
        for (Ability ability : destroyed) {
            this.sinks.tryEmitNext(Tuples.of(ability, true));
        }
    }

    @Override
    public void process(Collection<Ability> abilities) {
        if (!this.initialized) this.initialize();
        for (Ability ability : abilities) {
            this.sinks.tryEmitNext(Tuples.of(ability, false));
        }
    }
}
