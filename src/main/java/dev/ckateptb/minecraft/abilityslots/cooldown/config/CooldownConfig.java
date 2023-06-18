package dev.ckateptb.minecraft.abilityslots.cooldown.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class CooldownConfig {
    @Comment(value = "Use ability cooldowns to prevent spam", override = true)
    private boolean enabled = true;
    @Comment(value = "Global cooldown that applies to all abilities", override = true)
    private long global = 1000;
}
