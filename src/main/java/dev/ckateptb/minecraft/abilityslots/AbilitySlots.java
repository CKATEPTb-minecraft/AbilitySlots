package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import dev.ckateptb.minecraft.atom.Atom;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

// TODO Реализовать механику временных биндов (на примере WaterArms)

public class AbilitySlots extends JavaPlugin {
    private static Logger logger;
    @Getter
    private static AbilitySlots plugin;

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
    }

    public void reload() {
        Atom.syncScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new AbilitySlotsReloadEvent()));
    }
}