package dev.ckateptb.minecraft.abilityslots.predicate;


import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;

public interface Conditional<T> {
    boolean matches(AbilityUser user, T object);
}
