package dev.ckateptb.minecraft.abilityslots.command.preset.create.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class PresetCreateConfig {
    @Comment("Allowed placeholders: %preset_name%")
    private String failed = "&bPreset named &e%preset_name%&b already exists";
    @Comment("Allowed placeholders: %preset_name%")
    private String reply = "&bThe preset named &e%preset_name%&b was successfully created";
}
