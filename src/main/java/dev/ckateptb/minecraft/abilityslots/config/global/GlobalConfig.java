package dev.ckateptb.minecraft.abilityslots.config.global;

import dev.ckateptb.minecraft.abilityslots.ability.board.config.AbilityBoardConfig;
import dev.ckateptb.minecraft.abilityslots.ability.processor.config.AbilityProcessorConfig;
import dev.ckateptb.minecraft.abilityslots.cooldown.config.CooldownConfig;
import dev.ckateptb.minecraft.abilityslots.database.config.DatabaseConfig;
import dev.ckateptb.minecraft.abilityslots.energy.config.EnergyConfig;
import dev.ckateptb.minecraft.abilityslots.protection.config.ProtectionConfig;
import lombok.Getter;

@Getter
public class GlobalConfig {
    private AbilityBoardConfig board = new AbilityBoardConfig();
    private EnergyConfig energy = new EnergyConfig();
    private CooldownConfig cooldown = new CooldownConfig();
    private DatabaseConfig database = new DatabaseConfig();
    private ProtectionConfig protection = new ProtectionConfig();
    private AbilityProcessorConfig processor = new AbilityProcessorConfig();
}
