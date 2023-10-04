package dev.ckateptb.minecraft.abilityslots.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configurable {
    String name() default "";

    String comment() default "";

    // Calculation type
    Modifier modifier() default Modifier.NONE;

    // Can be 'damage', 'distance', 'radius', 'cooldown', 'energy', 'duration' or sth custom
    String modifierName() default "";
}
