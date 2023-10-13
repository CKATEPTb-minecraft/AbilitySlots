package dev.ckateptb.minecraft.abilityslots.ability.collision.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.ICollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.enums.AbilityCollisionResult;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.colliders.Collider;
import lombok.CustomLog;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collection;
import java.util.Objects;

@Component
@CustomLog
public class AbilityCollisionService {
    public Flux<CollidableAbility> findCollided(Collection<Ability> abilities) {
        return this.intersect(Flux.fromIterable(abilities)
                .filter(ability -> {
                    if (!ability.isLocked() && ability instanceof CollidableAbility collidable) {
                        Collection<Collider> colliders = collidable.getColliders();
                        return colliders != null && !colliders.isEmpty();
                    }
                    return false;
                })
                .cast(CollidableAbility.class));
    }

    private Flux<CollidableAbility> intersect(Flux<CollidableAbility> flux) {
        final AbilityCollisionResult ignore = AbilityCollisionResult.CONTINUE;
        final AbilityCollisionResult destroy = AbilityCollisionResult.DESTROY;
        return flux.join(flux, f -> Flux.never(), f -> Flux.never(), Tuples::of)
                .filter(abilities -> {
                    CollidableAbility first = abilities.getT1();
                    CollidableAbility second = abilities.getT2();
                    if (first == second) return false;
                    if (first.getUser().getUniqueId().equals(second.getUser().getUniqueId())) return false;
//                    ICollisionDeclaration firstDeclaration = first.getCollisionDeclaration();
                    ICollisionDeclaration secondDeclaration = second.getCollisionDeclaration();
                    if (secondDeclaration == null) return false;
//                    boolean removeSecond = firstDeclaration.isDestruct(second);
                    boolean removeFirst = secondDeclaration.isDestruct(first);
                    if (!removeFirst/* && !removeSecond*/) return false;
                    AbilityCollisionResult firstResult = ignore;
//                    AbilityCollisionResult secondResult = ignore;
                    for (Collider firstCollider : first.getColliders().stream().filter(Objects::nonNull).toList()) {
                        if (firstResult == destroy) continue;
                        for (Collider secondCollider : second.getColliders().stream().filter(Objects::nonNull).toList()) {
                            if (firstResult == destroy) continue;
//                            if(secondResult == destroy) continue;;
                            boolean firstIntersects = firstCollider.intersects(secondCollider);
                            boolean secondIntersects = secondCollider.intersects(firstCollider);
                            if (firstIntersects || secondIntersects) {
                                try {
                                    firstResult = first.onCollide(firstCollider, second, secondCollider);
                                } catch (Throwable throwable) {
                                    IAbilityDeclaration<? extends CollidableAbility> declaration = first.getDeclaration();
                                    log.error(MessageFormatter.arrayFormat(
                                            "There was an error processing ability collision {} and has" +
                                                    " been called back. Contact the author {}.",
                                            new Object[]{declaration.getName(), declaration.getAuthor()}
                                    ).getMessage(), throwable);
                                    firstResult = destroy;
                                }
//                                if (removeSecond && secondResult == ignore) {
//                                    secondResult = second.onCollide(secondCollider, first, firstCollider);
//                                }
                            }
                        }
                    }
                    return firstResult == AbilityCollisionResult.DESTROY;
                })
                .map(Tuple2::getT1)
                //Remove duplicates
                .groupBy(ability -> ability)
                .map(GroupedFlux::key);
    }
}
