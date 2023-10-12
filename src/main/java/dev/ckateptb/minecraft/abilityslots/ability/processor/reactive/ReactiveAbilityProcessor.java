package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;

import java.util.Collection;

public interface ReactiveAbilityProcessor {
    void process(Collection<Ability> abilities);
    void destroy(Ability... destroyed);
}
