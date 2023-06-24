package dev.ckateptb.minecraft.abilityslots.ability.collision.declaration;

import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;

import java.util.Collection;

public interface ICollisionDeclaration {
    /**
     * @param ability Переданная способность.
     * @return может ли текущая способность, разрушить коллайдер переданной способности.
     */
    boolean isDestruct(CollidableAbility ability);

    /**
     * @return Список классов способностей, который текущая способность может разрушить.
     */
    Collection<Class<? extends CollidableAbility>> getDestructible();
}
