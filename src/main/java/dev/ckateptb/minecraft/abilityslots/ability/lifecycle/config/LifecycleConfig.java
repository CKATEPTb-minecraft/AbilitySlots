package dev.ckateptb.minecraft.abilityslots.ability.lifecycle.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class LifecycleConfig {
    @Comment("""
            Available options:
            SYNC - Process abilities on the main thread
            ASYNC - Process abilities on the async thread
            SMART - Process abilities on the main thread. If necessary (TPS drawdown below 18), they will switch to asynchronous processing""")
    private String ticking = Ticking.SMART.name();
    @Comment("Limit the duration of ability processing per tick in milliseconds")
    private long threshold = 100;

    public Ticking getTicking() {
        return Ticking.valueOf(this.ticking);
    }

    public enum Ticking {
        SYNC,
        ASYNC,
        SMART
    }
}
