package dev.ckateptb.minecraft.abilityslots.ability.category.annotation;

import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Данная аннотация применяется к классам, наследующим {@link AbilityCategory},
 * для автоматической генерации декларации способности. См.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryDeclaration {

    /**
     * @return Системное имя категории.
     */
    String name();

    /**
     * @return Отображаемое имя категории.
     */
    String displayName();

    /**
     * @return Полное описание способности.
     */
    String description();

    /**
     * @return Префикс для способностей данной категории.
     */
    String abilityPrefix();
}
