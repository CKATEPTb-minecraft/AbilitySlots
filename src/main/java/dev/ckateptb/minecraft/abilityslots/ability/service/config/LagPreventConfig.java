package dev.ckateptb.minecraft.abilityslots.ability.service.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class LagPreventConfig {
    @Comment("To avoid server load from third-party developers' ineptitude, each ability has a timeout for a tick. If the ability goes beyond the specified time (in ms), it is considered stuck and will be instantly destroyed.")
    private long lagThreshold = 5000;
    @Comment("Report abilities that are subsequently destroyed by lag threshold to the console.")
    private boolean alertDestroyed = true;
    @Comment("Stop Faucet, which prevents the server from freezing if processing abilities takes too long.")
    private long dropAllThreshold = 10000;
}
