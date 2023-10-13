package dev.ckateptb.minecraft.abilityslots.ability.lifecycle;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public abstract class AbstractAbilityLifecycle<T> implements AbilityLifecycle<T> {
    private final Sinks.Many<T> sinks = Sinks.many().unicast().onBackpressureBuffer();
    protected final Flux<T> flux = this.sinks.asFlux();

    @Override
    public void emit(T value) {
        this.sinks.tryEmitNext(value);
    }
}
