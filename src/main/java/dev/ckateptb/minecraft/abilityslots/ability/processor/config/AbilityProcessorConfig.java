package dev.ckateptb.minecraft.abilityslots.ability.processor.config;

import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.config.ReactiveProcessorConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class AbilityProcessorConfig {

    @Comment("""
            Ability processing method:
             SYNC - Process abilities on the server thread
             ASYNC - Process abilities in an asynchronous server thread
             REACTIVE - Process abilities in reactive threads
             SMART - Process abilities in a synchronous thread and switch to a reactive during TPS < 18""")
    private String processor = AbilityProcessorType.SYNC.name();
    private ReactiveProcessorConfig reactive = new ReactiveProcessorConfig();

    public AbilityProcessorType getProcessor() {
        return AbilityProcessorType.valueOf(this.processor);
    }

    public enum AbilityProcessorType {
        SYNC,
        ASYNC,
        REACTIVE,
        SMART
    }
}
