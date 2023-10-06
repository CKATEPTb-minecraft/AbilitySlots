package dev.ckateptb.minecraft.abilityslots.ability.service.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class LagPreventConfig {
    @Comment("To avoid server load from third-party developers' ineptitude, each ability has a timeout for a tick. If the ability goes beyond the specified time (in ms), it is considered stuck and will be instantly destroyed.")
    private long lagThreshold = 500;
    @Comment("Report abilities that are subsequently destroyed by lag threshold to the console.")
    private boolean alertDestroyed = true;
    @Comment("Stop Faucet, which prevents the server from freezing if processing abilities takes too long.")
    private long dropAllThreshold = 1000;
    @Comment("It is not recommended to change this value.\n" +
            "Available values:\n" +
            "SERVER_SYNC - Synchronized ability ticking\n" +
            "SERVER_ASYNC - Asynchronous ability ticking\n" +
            "SINGLE - Tick abilities in a thread optimized for fast operations\n" +
            "PARALLEL - Tick abilities in parallel threads\n" +
            "ELASTIC - Tick ability in elastic thread")
    private String tickIn = TickIn.SINGLE.name();
    @Comment("It is not recommended to change this value.\n" +
            "Tick abilities parallel")
    private boolean parallel = false;
    @Comment("It is not recommended to change this value.\n" +
            "https://www.baeldung.com/java-daemon-thread")
    private boolean daemon = true;
    @Comment("It is not recommended to change this value.")
    private int threadCount = 20;

    public enum TickIn {
        SERVER_SYNC,
        SERVER_ASYNC,
        SINGLE,
        PARALLEL,
        ELASTIC,

    }

    public TickIn getTickIn() {
        return TickIn.valueOf(this.tickIn);
    }
}
