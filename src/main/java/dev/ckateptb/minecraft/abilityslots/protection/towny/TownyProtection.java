package dev.ckateptb.minecraft.abilityslots.protection.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.protection.AbstractProtection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class TownyProtection extends AbstractProtection {
    private final TownyAPI api;

    public TownyProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        this.api = TownyAPI.getInstance();
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!this.config.getGlobal().getProtection().isRespectTowny()) return true;
        if (entity instanceof Player player) {
            return PlayerCacheUtil.getCachePermission(player, location, Material.DIRT, TownyPermission.ActionType.BUILD);
        }
        TownBlock townBlock = this.api.getTownBlock(location);
        return townBlock == null || !townBlock.hasTown();
    }
}
