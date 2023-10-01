package dev.ckateptb.minecraft.abilityslots.command.bind.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class BindConfig {
    @Comment("Allowed placeholders: %ability_name%, %ability_display_name%, %category_ability_prefix%, %slot%")
    private String failed = "&bYou can't bind this ability";
    @Comment("Allowed placeholders: %ability_name%, %ability_display_name%, %category_ability_prefix%, %slot%")
    private String reply = "&bYou have successfully bound the %category_ability_prefix%%ability_display_name%&b ability to slot &n%slot%";
}
