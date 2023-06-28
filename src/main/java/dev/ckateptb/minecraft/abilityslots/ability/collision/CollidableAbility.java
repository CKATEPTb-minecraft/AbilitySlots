package dev.ckateptb.minecraft.abilityslots.ability.collision;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.ICollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.enums.AbilityCollisionResult;
import dev.ckateptb.minecraft.colliders.Collider;

import java.util.Collection;

/**
 * Способности реализующие этот интерфейс принимают участие в расчете столкновений.
 */
public abstract class CollidableAbility extends Ability {
    /**
     * @return Экземпляр декларации, описывающей столкновения для этой способности.
     */
    abstract ICollisionDeclaration getCollisionDeclaration();

    /**
     * Чтобы способность принимала участие в расчете столкновений, она должна иметь как минимум один {@link Collider}.
     * @return Перечень {@link Collider}, которые участвуют в расчете столкновений.
     */
    abstract Collection<Collider> getColliders();

    /**
     * Вызов данного метода управляется {@link dev.ckateptb.minecraft.abilityslots.ability.service.AbilityInstanceService}.
     * Данный метод вызывается, когда другая способность, имеющая приоритет в столкновениях, столкнулась с текущей.
     * По задуманной логике вы должны обработать это столкновение в пользу другой способности.
     * @param collider Коллайдер, который столкнулся с коллайдером другой способности.
     * @param other Другая способность, коллайдер которой спровоцировал вызов данного метода.
     * @param otherCollider Коллайдер другой способности, который спровоцировал вызов данного метода.
     * @return стоит ли продолжать обрабатывать способность, несмотря на столкновение.
     */
    abstract AbilityCollisionResult onCollide(Collider collider, CollidableAbility other, Collider otherCollider);
}
