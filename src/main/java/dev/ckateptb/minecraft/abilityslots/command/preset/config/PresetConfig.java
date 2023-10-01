package dev.ckateptb.minecraft.abilityslots.command.preset.config;

import dev.ckateptb.minecraft.abilityslots.command.preset.bind.config.PresetBindConfig;
import dev.ckateptb.minecraft.abilityslots.command.preset.create.config.PresetCreateConfig;
import dev.ckateptb.minecraft.abilityslots.command.preset.delete.config.PresetDeleteConfig;
import dev.ckateptb.minecraft.abilityslots.command.preset.list.config.PresetListConfig;
import lombok.Getter;

@Getter
public class PresetConfig {
    private PresetListConfig list = new PresetListConfig();
    private PresetCreateConfig create = new PresetCreateConfig();
    private PresetDeleteConfig delete = new PresetDeleteConfig();
    private PresetBindConfig bind = new PresetBindConfig();
}
