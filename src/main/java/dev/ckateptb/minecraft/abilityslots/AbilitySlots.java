package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class AbilitySlots extends JavaPlugin {
    private static AbilitySlots plugin;

    public static AbilitySlots getPlugin() {
        return plugin;
    }

    public AbilitySlots() {
        plugin = this;
        IoC.scan(AbilitySlots.class);
        IoC.registerBean(this);
    }
}