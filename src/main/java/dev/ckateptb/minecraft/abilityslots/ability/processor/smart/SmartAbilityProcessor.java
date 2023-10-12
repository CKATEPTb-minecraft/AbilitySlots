package dev.ckateptb.minecraft.abilityslots.ability.processor.smart;

import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.processor.AbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.AbstractAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.ReactiveAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.adapter.ReactiveAbilityProcessorAdapter;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.config.ReactiveProcessorConfig;
import dev.ckateptb.minecraft.abilityslots.ability.processor.sync.SyncAbilityProcessor;
import org.bukkit.Bukkit;

public class SmartAbilityProcessor extends AbstractAbilityProcessor {
    private final SyncAbilityProcessor sync;
    private final ReactiveAbilityProcessorAdapter reactive;

    public SmartAbilityProcessor(AbilityCollisionService collisionService, ReactiveProcessorConfig config) {
        super(collisionService);
        this.sync = new SyncAbilityProcessor(collisionService);
        this.reactive = new ReactiveAbilityProcessorAdapter(collisionService, config);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void tick() {
        double tps = Bukkit.getServer().getTPS()[0];
        if (tps > 18) {
            this.sync.tick();
        } else {
            this.reactive.tick();
        }
    }
}
