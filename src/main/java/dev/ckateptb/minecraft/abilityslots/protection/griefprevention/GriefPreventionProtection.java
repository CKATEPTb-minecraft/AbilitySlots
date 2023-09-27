package dev.ckateptb.minecraft.abilityslots.protection.griefprevention;

import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.protection.AbstractProtection;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class GriefPreventionProtection extends AbstractProtection {
    private final GriefPrevention griefPrevention;

    public GriefPreventionProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        this.griefPrevention = (GriefPrevention) plugin;
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!this.config.getGlobal().getProtection().isRespectGriefPrevention()) return true;
        if (entity instanceof Player player) {
            String reason = this.griefPrevention.allowBuild(player, location);
            Claim claim = this.griefPrevention.dataStore.getClaimAt(location, true, null);
            return reason == null || claim == null || claim.siegeData != null;
        }
        return true;
    }
}
