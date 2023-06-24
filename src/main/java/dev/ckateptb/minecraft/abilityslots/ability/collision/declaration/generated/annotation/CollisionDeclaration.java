package dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.annotation;

import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Данная аннотация применяется к классам, реализующим интерфейс {@link CollidableAbility},
 * для автоматической генерации декларации столкновений.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CollisionDeclaration {
    /**
     * @return Перечень способностей, которые должны пострадать, при столкновении с декларируемой.
     */
    Class<? extends CollidableAbility>[] destructible() default {};
}