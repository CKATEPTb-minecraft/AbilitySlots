package dev.ckateptb.minecraft.abilityslots.ability.lifecycle;

public interface AbilityLifecycle<T> {
    void process();

    void emit(T value);
}
