package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public class AbilitySlots extends JavaPlugin {
    private static AbilitySlots plugin;
    private static Logger logger;

    public static Logger log() {
        return logger;
    }

    public static AbilitySlots getPlugin() {
        return plugin;
    }

    public AbilitySlots() {
        plugin = this;
        logger = this.getSLF4JLogger();
        IoC.scan(AbilitySlots.class);
        IoC.registerBean(this);
    }

    public void reload() {
        Bukkit.getPluginManager().callEvent(new AbilitySlotsReloadEvent());
    }
}