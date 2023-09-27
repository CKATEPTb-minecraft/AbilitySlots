package dev.ckateptb.minecraft.abilityslots.protection;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public interface Protection {
    boolean canUse(LivingEntity entity, Location location);
}