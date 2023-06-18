package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.entity.IAbilityTarget;
import org.bukkit.entity.Player;

public class PlayerAbilityUser<T extends Player> extends LivingEntityAbilityUser<T> {
    protected PlayerAbilityUser(IAbilityTarget<T> entity) {
        super(entity);
    }
}
