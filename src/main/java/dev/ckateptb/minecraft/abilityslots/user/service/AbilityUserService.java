package dev.ckateptb.minecraft.abilityslots.user.service;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.LivingEntityAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class AbilityUserService implements Listener {
    private final Map<UUID, AbilityUser> users = new HashMap<>();
    @Getter
    private final AbilitySlotsConfig config;

    public synchronized AbilityUser getAbilityUser(LivingEntity livingEntity) {
        throw new NotImplementedException();
//        return this.users.computeIfAbsent(livingEntity.getUniqueId(), key -> new LivingEntityAbilityUser(livingEntity, this));
    }

    public synchronized PlayerAbilityUser getAbilityUser(Player player) {
        PlayerAbilityUser user = (PlayerAbilityUser) this.users.computeIfAbsent(player.getUniqueId(), key -> new PlayerAbilityUser(player, this));
        user.updatePlayerAdapter(player);
        return user;
    }

    public synchronized Stream<AbilityUser> getAbilityUsers() {
        return this.users.values().stream();
    }

    @EventHandler
    private void on(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player player) {
            PlayerAbilityUser user = this.getAbilityUser(player);
            user.hideEnergyBoard();
            user.showEnergyBoard();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void on(EntityRemoveFromWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            if (!(living instanceof Player)) {
                this.users.remove(living.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void on(AbilitySlotsReloadEvent event) {
        this.users.clear();
    }
}
