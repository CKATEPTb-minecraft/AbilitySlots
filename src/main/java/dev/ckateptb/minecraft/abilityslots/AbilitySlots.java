package dev.ckateptb.minecraft.abilityslots;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.common.tableclothcontainer.event.ComponentRegisterEvent;
import dev.ckateptb.common.tableclothevent.EventBus;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

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
        EventBus.GLOBAL.registerEventHandler(ComponentRegisterEvent.class, (event) -> logger.info("registering: " + event.getClazz().getName()));
        IoC.registerBean(this, AbilitySlots.class);
        IoC.scan(AbilitySlots.class);
    }

    @Override
    public void onEnable() {
        this.reload();
    }

    public void reload() {
        logger.info("call reload");
        Bukkit.getPluginManager().callEvent(new AbilitySlotsReloadEvent());
    }
}