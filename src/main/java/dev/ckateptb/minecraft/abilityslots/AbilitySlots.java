package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AbilitySlots extends JavaPlugin {
    private static AbilitySlots plugin;
    private static AbilitySlotsConfig config;

    @NotNull
    public static AbilitySlotsConfig config() {
        return config == null ? config = IoC.getBean(AbilitySlotsConfig.class) : config;
    }

    public static AbilitySlots getPlugin() {
        return plugin;
    }

    public AbilitySlots() {
        plugin = this;
        IoC.scan(AbilitySlots.class);
        IoC.registerBean(this);
    }
}