package dev.ckateptb.minecraft.abilityslots.ability.processor;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;

import java.util.stream.Stream;

public interface AbilityProcessor {
    void process();

    void destroy(Ability... destroyed);

    void destroyAll();

    Stream<Ability> instances();

    void register(Ability ability);

    int getDelay();

    int getRate();

    boolean isAsync();
}
