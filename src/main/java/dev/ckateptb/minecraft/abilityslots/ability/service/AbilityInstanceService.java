package dev.ckateptb.minecraft.abilityslots.ability.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.processor.AbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.async.AsyncAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.config.AbilityProcessorConfig;
import dev.ckateptb.minecraft.abilityslots.ability.processor.reactive.adapter.ReactiveAbilityProcessorAdapter;
import dev.ckateptb.minecraft.abilityslots.ability.processor.sync.SyncAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@CustomLog
@RequiredArgsConstructor
public class AbilityInstanceService implements Listener {
    private final AbilitySlotsConfig config;
    private final AbilityCollisionService collisionService;
    private BukkitTask task;
    private AbilityProcessor processor;

    public void register(Ability ability) {
        if (this.processor == null) return;
        this.processor.register(ability);
    }

    public synchronized Stream<Ability> instances(AbilityUser user) {
        if (this.processor == null) return Stream.empty();
        return this.processor.instances().filter(ability -> Objects.equals(user, ability.getUser()));
    }

    public synchronized Stream<Ability> instances(Class<? extends Ability> type) {
        if (this.processor == null) return Stream.empty();
        return this.processor.instances().filter(type::isInstance);
    }

    public synchronized Stream<Ability> instances() {
        if (this.processor == null) return Stream.empty();
        return this.processor.instances();
    }

    public synchronized void destroy(Ability... destroyed) {
        if (this.processor == null) return;
        this.processor.destroy(destroyed);
    }

    public synchronized void setAbilityProcessor(AbilityProcessor processor) {
        if (this.processor != null) {
            this.processor.destroyAll();
            this.task.cancel();
        }
        this.processor = processor;
        int delay = this.processor.getDelay();
        int rate = this.processor.getRate();
        AbilitySlots plugin = AbilitySlots.getPlugin();
        if (this.processor.isAsync()) {
            this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this.processor::process, delay, rate);
        } else {
            this.task = Bukkit.getScheduler().runTaskTimer(plugin, this.processor::process, delay, rate);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void on(AbilitySlotsReloadEvent event) {
        AbilityProcessorConfig config = this.config.getGlobal().getProcessor();
        this.setAbilityProcessor(switch (config.getProcessor()) {
            case SYNC -> new SyncAbilityProcessor(this.collisionService);
            case ASYNC -> new AsyncAbilityProcessor(this.collisionService);
            case REACTIVE -> new ReactiveAbilityProcessorAdapter(this.collisionService, config.getReactive());
            case SMART -> null;
        });
    }
}
