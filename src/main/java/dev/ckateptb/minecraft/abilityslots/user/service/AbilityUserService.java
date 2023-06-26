package dev.ckateptb.minecraft.abilityslots.user.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.LivingEntityAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

@Component
public class AbilityUserService implements Listener {
    private final Map<LivingEntity, AbilityUser> users = new HashMap<>();
    public AbilityUser getAbilityUser(LivingEntity livingEntity) {
        return this.users.computeIfAbsent(livingEntity, key -> {
            if(key instanceof Player player) {
                return new PlayerAbilityUser(player);
            } else return new LivingEntityAbilityUser(livingEntity);
        });
    }
}