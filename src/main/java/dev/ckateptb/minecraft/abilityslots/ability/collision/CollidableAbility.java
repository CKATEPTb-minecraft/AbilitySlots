package dev.ckateptb.minecraft.abilityslots.ability.collision;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.ICollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.enums.AbilityCollisionResult;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.colliders.Collider;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Способности реализующие этот интерфейс принимают участие в расчете столкновений.
 */
public abstract class CollidableAbility extends Ability {
    /**
     * Чтобы способность принимала участие в расчете столкновений, она должна иметь как минимум один {@link Collider}.
     *
     * @return Перечень {@link Collider}, которые участвуют в расчете столкновений.
     */
    public abstract Collection<Collider> getColliders();

    /**
     * Данный метод вызывается, когда другая способность, имеющая приоритет в столкновениях, столкнулась с текущей.
     * По задуманной логике вы должны обработать это столкновение в пользу другой способности.
     *
     * @param collider      Коллайдер, который столкнулся с коллайдером другой способности.
     * @param other         Другая способность, коллайдер которой спровоцировал вызов данного метода.
     * @param otherCollider Коллайдер другой способности, который спровоцировал вызов данного метода.
     * @return стоит ли продолжать обрабатывать способность, несмотря на столкновение.
     */
    public abstract AbilityCollisionResult onCollide(Collider collider, CollidableAbility other, Collider otherCollider);

    @Getter
    @Setter
    private ICollisionDeclaration collisionDeclaration;

    @Override
    @SuppressWarnings("all")
    public IAbilityDeclaration<? extends CollidableAbility> getDeclaration() {
        return (IAbilityDeclaration<? extends CollidableAbility>) this.declaration;
    }
}
