package dev.ckateptb.minecraft.abilityslots.protection.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.protection.AbstractProtection;
import lombok.CustomLog;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CustomLog
public final class WorldGuardProtection extends AbstractProtection {
    private final WorldGuard worldGuard;
    private StateFlag flag;

    public WorldGuardProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        this.worldGuard = WorldGuard.getInstance();
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            this.flag = new StateFlag("abilityslots", false);
            registry.register(flag);
        } catch (FlagConflictException e) {
            this.flag = Flags.BUILD;
            log.warn("Failed to register AbilitySlots flag for WorldGuard. Use the build flag");
            e.printStackTrace();
        }
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!this.config.getGlobal().getProtection().isRespectWorldGuard()) return true;
        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location worldGuardLocation = BukkitAdapter.adapt(location);
        if (entity instanceof Player player) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            World world = BukkitAdapter.adapt(location.getWorld());
            if (this.worldGuard.getPlatform().getSessionManager().hasBypass(localPlayer, world)) {
                return true;
            }
            return query.testState(worldGuardLocation, localPlayer, this.flag);
        }
        // Query WorldGuard to see if a non-member (entity) can build in a region.
        return query.testState(worldGuardLocation, list -> Association.NON_MEMBER, this.flag);
    }
}