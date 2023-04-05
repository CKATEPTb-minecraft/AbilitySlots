package dev.ckateptb.minecraft.abilityslots.collision;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.collision.enums.CollisionResult;
import dev.ckateptb.minecraft.colliders.Collider;

import java.util.Collection;

public interface ColliderHolder {
    Collection<Collider> getColliders();
    CollisionResult destroyCollider(Ability other, Collider otherCollider, Collider collider);
}
