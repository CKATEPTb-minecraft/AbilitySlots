package dev.ckateptb.minecraft.abilityslots.ability.board.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class AbilityBoardConfig {
    @Comment("Displaying current abilities in the scoreboard")
    private boolean enabled = true;
    private String header = "§f§lAbilities:";
    private String empty = "§8-- Empty --";
    @Comment("Allowed placeholders %category_prefix%, %ability%")
    private String ability = "%category_prefix%%ability%";
    @Comment("Allowed placeholders %category_prefix%, %ability%, %cooldown%")
    private String cooldown = "§m%category_prefix%%ability%§r%category_prefix% - %cooldown%";
    private String comboDivider = "§f§lCombos:";
}
