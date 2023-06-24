package dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.enums.SequenceAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityAction {
    Class<? extends Ability> ability();

    SequenceAction action();
}