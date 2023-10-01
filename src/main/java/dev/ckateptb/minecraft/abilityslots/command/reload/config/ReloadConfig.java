package dev.ckateptb.minecraft.abilityslots.command.reload.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class ReloadConfig {
    private String reply = "&6%abilityslots_plugin_name%&b successful reloaded";
    @Comment("Allowed placeholders: %player_name%")
    private String replyPlayer = "&bAbility user &e%player_name%&b successful reloaded";
}
