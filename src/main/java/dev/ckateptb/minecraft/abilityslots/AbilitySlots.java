package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import dev.ckateptb.minecraft.atom.scheduler.SyncScheduler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AbilitySlots extends JavaPlugin {
    private static Logger logger;
    @Getter
    private static AbilitySlots plugin;
    public final List<Runnable> executeOnEnable = new ArrayList<>();

    public static Logger log() {
        return logger;
    }

    public AbilitySlots() {
        plugin = this;
        logger = this.getSLF4JLogger();
        IoC.registerBean(this, AbilitySlots.class);
        IoC.scan(AbilitySlots.class);
    }

    @Override
    public void onEnable() {
        this.reload();
        this.executeOnEnable.removeIf(runnable -> {
            runnable.run();
            return true;
        });
    }

    public void reload() {
        new SyncScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new AbilitySlotsReloadEvent()));
    }

    public void executeOnEnable(Runnable runnable) {
        if (this.isEnabled()) runnable.run();
        else this.executeOnEnable.add(runnable);
    }
}