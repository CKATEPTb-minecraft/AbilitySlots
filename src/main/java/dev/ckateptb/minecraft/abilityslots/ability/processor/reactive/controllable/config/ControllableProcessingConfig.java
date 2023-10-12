package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.controllable.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class ControllableProcessingConfig {
    @Comment("To avoid server load from third-party developers' ineptitude, each ability has a timeout for a tick.\n" +
            "If the ability goes beyond the specified time (in ms),\n" +
            "it is considered stuck and will be instantly destroyed. Set 0 to disable")
    private long abilityThreshold = 500;
    @Comment("Report abilities that are subsequently destroyed by lag threshold to the console.")
    private boolean alertDestroyed = true;
    @Comment("Stop Faucet, which prevents the server from freezing if processing abilities takes too long.")
    private long tickThreshold = 1000;
}
