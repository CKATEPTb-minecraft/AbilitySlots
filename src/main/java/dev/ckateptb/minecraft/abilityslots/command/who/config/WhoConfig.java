package dev.ckateptb.minecraft.abilityslots.command.who.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class WhoConfig {
    @Comment("Allowed placeholders: %category_ability_prefix%, %ability_display_name%, %ability_name%, " +
            "%ability_author%, %ability_description%, %ability_instruction%, %slot%")
    private String abilityFormat = """
             &8↳ &b%slot% &8- &[%category_ability_prefix%%ability_display_name%&]&(hover:text &8Author: %category_ability_prefix%%ability_author%
            &8Description: %category_ability_prefix%%ability_description%
            &8Instruction: %category_ability_prefix%%ability_instruction%&)""";
    @Comment("Allowed placeholders: %slot%")
    private String empty = " &8↳ &b%slot% &8- &8Empty";
    @Comment("Allowed placeholders: %player_name%")
    private String reply = "&bThe %player_name% player's ability board consists of:";
}
