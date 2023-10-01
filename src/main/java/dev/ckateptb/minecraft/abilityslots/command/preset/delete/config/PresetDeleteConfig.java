package dev.ckateptb.minecraft.abilityslots.command.preset.delete.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class PresetDeleteConfig {
    @Comment("Allowed placeholders: %preset_name%")
    private String failed = "&bPreset named &e%preset_name%&b not found";
    @Comment("Allowed placeholders: %preset_name%")
    private String reply = "&bThe preset named &e%preset_name%&b was successfully deleted";
}
