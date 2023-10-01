package dev.ckateptb.minecraft.abilityslots.command.preset.bind.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class PresetBindConfig {
    @Comment("Allowed placeholders: %preset_name%")
    private String reply = "&bPreset named &e%preset_name%&b bound successful";
    @Comment("Allowed placeholders: %preset_name%")
    private String failed = "&bPreset named &e%preset_name%&b not found";
}
