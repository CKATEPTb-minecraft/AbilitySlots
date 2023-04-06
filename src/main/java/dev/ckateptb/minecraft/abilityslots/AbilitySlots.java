package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class AbilitySlots extends JavaPlugin {
    private static AbilitySlots plugin;
    public final static Supplier<Logger> log = () -> plugin.getSLF4JLogger();

    public static AbilitySlots getPlugin() {
        return plugin;
    }

    public AbilitySlots() {
        plugin = this;
        IoC.scan(AbilitySlots.class);
        IoC.registerBean(this);
    }

    public void reload() {
        Bukkit.getPluginManager().callEvent(new AbilitySlotsReloadEvent());
    }
}