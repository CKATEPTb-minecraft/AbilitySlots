package dev.ckateptb.minecraft.abilityslots.config.global;

import dev.ckateptb.minecraft.abilityslots.cooldown.config.CooldownConfig;
import dev.ckateptb.minecraft.abilityslots.database.config.DatabaseConfig;
import dev.ckateptb.minecraft.abilityslots.energy.config.EnergyConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@Getter
public class GlobalConfig {
    private EnergyConfig energy = new EnergyConfig();
    private CooldownConfig cooldown = new CooldownConfig();
    private DatabaseConfig database = new DatabaseConfig();
}
