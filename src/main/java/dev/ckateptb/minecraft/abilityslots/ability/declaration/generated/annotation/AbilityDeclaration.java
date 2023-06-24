package dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Данная аннотация применяется к классам, реализующим интерфейс {@link Ability},
 * для автоматической генерации декларации способности. См.
 * <p>{@link dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration}</p>
 * <p>{@link dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.GeneratedAbilityDeclaration}</p>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityDeclaration {

    /**
     * @return Системное имя способности.
     */
    String name();

    /**
     * @return Клас реализующий категорию данной способности.
     */
    Class<? extends AbilityCategory> category();


    /**
     * @return Никнейм автора способности.
     */
    String author();

    /**
     * @return Отображаемое имя способности.
     */
    String displayName();

    /**
     * @return Полное описание способности.
     */
    String description();

    /**
     * @return Полное руководство по применению способности.
     */
    String instruction();

    /**
     * @return Способы активации для указанной способности.
     */
    ActivationMethod[] activators();

    /**
     * @return Возможно ли привязать способность к слоту хот-бара.
     */
    boolean bindable() default true;
}
