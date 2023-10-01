package dev.ckateptb.minecraft.abilityslots.command.preset.list.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class PresetListConfig {
    private String reply = "&bList of your presets:";
    private String empty = "&bYou don't have presets";
    @Comment("Allowed placeholders: %preset_name%")
    private String presetFormat = "&8↳ &e%preset_name% &8- " +
            "&[&ause&]&(hover:text &7Click to use this preset&)&(click:suggest /abilityslots preset bind %preset_name%&)&8/" +
            "&[&cdelete&]&(hover:text &7Click to delete this preset&)&(click:suggest /abilityslots preset delete %preset_name%&)";
}
