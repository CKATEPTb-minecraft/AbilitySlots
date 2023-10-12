package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.config;

import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.controllable.config.ControllableProcessingConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.Map;

import static reactor.core.scheduler.Schedulers.*;

@Getter
public class ReactiveProcessorConfig {
    @Comment("""
            Ticking thread:
             SINGLE - Separate thread designed for fast operations
             PARALLEL - Multiple threads running in parallel per each ability
             ELASTIC - Flexible threads that can expand as needed""")
    private String scheduler = ReactiveScheduler.SINGLE.name();
    @Comment("Daemon threads are low-priority threads whose only role is to provide services to user threads")
    private boolean daemon = false;
    @Comment("""
            Processing method:
             EMIT - A single handler that takes signals about how to handle the ability
             CONTROLLABLE - Controls the processing of each ability to limit its time
             DEFAULT - Standard ability processing""")
    private String mode = ReactiveMode.CONTROLLABLE.name();
    @Comment("Parameters for CONTROLLABLE processing mode")
    private ControllableProcessingConfig controllable = new ControllableProcessingConfig();

    public Scheduler getScheduler() {
        return SchedulersCache.getScheduler(ReactiveScheduler.valueOf(this.scheduler), this.daemon);
    }

    public ReactiveMode getMode() {
        return ReactiveMode.valueOf(this.mode);
    }

    public enum ReactiveScheduler {
        SINGLE,
        PARALLEL,
        ELASTIC
    }

    public enum ReactiveMode {
        EMIT,
        CONTROLLABLE,
        DEFAULT
    }

    public static class SchedulersCache {
        private final static Map<Tuple2<ReactiveScheduler, Boolean>, Scheduler> cache = new HashMap<>();

        public static Scheduler getScheduler(ReactiveScheduler scheduler, boolean daemon) {
            Tuple2<ReactiveScheduler, Boolean> objects = Tuples.of(scheduler, daemon);
            return cache.computeIfAbsent(objects, (key) -> switch (scheduler) {
                case SINGLE -> Schedulers.newSingle("abilities-s", false);
                case PARALLEL -> Schedulers.newParallel("abilities-p", DEFAULT_POOL_SIZE, false);
                case ELASTIC -> Schedulers.newBoundedElastic(
                        DEFAULT_BOUNDED_ELASTIC_SIZE,
                        DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
                        "abilities-e", 1800, false
                );
            });
        }
    }
}
