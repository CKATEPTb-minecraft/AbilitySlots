package dev.ckateptb.minecraft.abilityslots.ability.board.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class AbilityBoardConfig {
    @Comment("Displaying current abilities in the scoreboard")
    private boolean enabled = true;
    private String header = "§f§lAbilities:";
    private String empty = "§8-- Empty --";
    private String comboDivider = "§f§lCombos:";
}
