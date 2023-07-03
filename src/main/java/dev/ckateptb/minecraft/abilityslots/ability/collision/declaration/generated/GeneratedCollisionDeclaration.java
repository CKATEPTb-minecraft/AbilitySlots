package dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated;

import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.ICollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.annotation.CollisionDeclaration;
import lombok.Getter;

import java.util.Set;

/**
 * Экземпляр данного класса создается автоматически для способностей отмеченных аннотацией {@link CollisionDeclaration}
 */
@Getter
public class GeneratedCollisionDeclaration implements ICollisionDeclaration {
    private final Set<Class<? extends CollidableAbility>> destructible;

    public GeneratedCollisionDeclaration(CollisionDeclaration declaration) {
        this(Set.of(declaration.destructible()));
    }

    public GeneratedCollisionDeclaration(Set<Class<? extends CollidableAbility>> destructible) {
        this.destructible = destructible;
    }

    @Override
    public boolean isDestruct(CollidableAbility ability) {
        return this.destructible.contains(ability.getClass());
    }
}
