package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.adapter;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.processor.AbstractAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.ReactiveAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.config.ReactiveProcessorConfig;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.emit.EmitAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.parallel.ParallelAbilityAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.controllable.ControllableAbilityAbilityProcessor;
import lombok.CustomLog;
import reactor.core.scheduler.Scheduler;

import java.util.ArrayList;

@CustomLog
public class ReactiveAbilityProcessorAdapter extends AbstractAbilityProcessor {
    private final ReactiveAbilityProcessor processor;

    public ReactiveAbilityProcessorAdapter(AbilityCollisionService collisionService, ReactiveProcessorConfig config) {
        super(collisionService);
        Scheduler scheduler = config.getScheduler();
        this.processor = switch (config.getMode()) {
            case EMIT -> new EmitAbilityProcessor(scheduler);
            case CONTROLLABLE -> new ControllableAbilityAbilityProcessor(scheduler, config.getControllable());
            case DEFAULT -> new ParallelAbilityAbilityProcessor();
        };
    }

    @Override
    public synchronized void tick() {
        this.processor.process(new ArrayList<>(this.abilities));
    }

    @Override
    public synchronized void destroy(Ability... destroyed) {
        for (Ability ability : destroyed) {
            this.abilities.remove(ability);
        }
        this.processor.destroy(destroyed);
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
