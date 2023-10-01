package dev.ckateptb.minecraft.abilityslots.command.clear.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class ClearConfig {
    private String reply = "&bYou have cleared the ability board";
    @Comment("Allowed placeholders: %slot%")
    private String replySlot = "&bYou cleared an ability from slot &n%slot%";

}
