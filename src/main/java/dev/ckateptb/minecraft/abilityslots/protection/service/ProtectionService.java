package dev.ckateptb.minecraft.abilityslots.protection.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.entity.LivingEntityAbilityTarget;
import dev.ckateptb.minecraft.abilityslots.protection.Protection;
import dev.ckateptb.minecraft.abilityslots.protection.griefprevention.GriefPreventionProtection;
import dev.ckateptb.minecraft.abilityslots.protection.lwc.LWCProtection;
import dev.ckateptb.minecraft.abilityslots.protection.towny.TownyProtection;
import dev.ckateptb.minecraft.abilityslots.protection.worldguard.WorldGuardProtection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Getter
@Component
public class ProtectionService implements Listener, Iterable<Protection> {
    private final AbilitySlotsConfig config;
    private final Set<Protection> protectionPlugins = new HashSet<>();
    // For optimize caching, we must use Block, because its location is always static without yaw and pitch
    private final Map<UUID, Cache<BlockPos, Boolean>> cache = new HashMap<>();

    public ProtectionService(AbilitySlotsConfig config) {
        this.config = config;
        register("WorldGuard", plugin -> new WorldGuardProtection(plugin, config));
        register("GriefPrevention", plugin -> new GriefPreventionProtection(plugin, config));
        register("Towny", plugin -> new TownyProtection(plugin, config));
        register("LWC", plugin -> new LWCProtection(plugin, config));
    }

    private void register(String name, Function<Plugin, Protection> factory) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            Protection protection = factory.apply(plugin);
            this.protectionPlugins.add(protection);
            log.info("Registered protection for " + name);
        }
    }

    public synchronized boolean canUse(LivingEntityAbilityTarget user, Location location) {
        LivingEntity entity = user.getAdapter().getHandle_();
        UUID uuid = user.getUniqueId();
        return this.cache.computeIfAbsent(uuid, key ->
                Caffeine.newBuilder().expireAfterAccess(Duration.ofMillis(this.config.getGlobal().getProtection().getCacheDuration())).build()
        ).get(new BlockPos(location), loc -> this.stream().allMatch(protection -> protection.canUse(entity, location)));
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        this.cache.remove(entity.getUniqueId());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        this.cache.remove(event.getPlayer().getUniqueId());
    }

    public Stream<Protection> stream() {
        return this.protectionPlugins.stream();
    }

    @Override
    public Iterator<Protection> iterator() {
        return Collections.unmodifiableCollection(this.protectionPlugins).iterator();
    }

    private static class BlockPos {
        private final int x;
        private final int y;
        private final int z;
        private final World world;

        public BlockPos(Location location) {
            this.world = location.getWorld();
            this.x = location.getBlockX();
            this.y = location.getBlockY();
            this.z = location.getBlockZ();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof BlockPos other)) return false;
            return this.x == other.x && this.y == other.y && this.z == other.z && this.world.equals(other.world);
        }

        @Override
        public int hashCode() {
            return ((this.y + this.z * 31) * 31 + this.x) ^ this.world.hashCode();
        }
    }
}
