package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.entity.LivingEntityAbilityTarget;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.entity.LivingEntity;

public abstract class LivingEntityAbilityUser extends LivingEntityAbilityTarget implements AbilityUser {
    public LivingEntityAbilityUser(LivingEntity livingEntity, AbilityUserService service) {
        super(livingEntity);
    }

}
