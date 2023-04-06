package dev.ckateptb.minecraft.abilityslots.ability.collision.declaration;

import dev.ckateptb.minecraft.abilityslots.ability.collision.ICollidableAbility;

import java.util.Collection;

public interface ICollisionDeclaration {
    /**
     * @param ability Переданная способность.
     * @return может ли текущая способность, разрушить коллайдер переданной способности.
     */
    boolean isDestruct(ICollidableAbility ability);

    /**
     * @return Список классов способностей, который текущая способность может разрушить.
     */
    Collection<Class<? extends ICollidableAbility>> getDestructible();
}
