package dev.ckateptb.minecraft.abilityslots.config;

import dev.ckateptb.common.tableclothconfig.hocon.HoconConfig;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.cooldown.config.CooldownConfig;
import dev.ckateptb.minecraft.abilityslots.energy.config.EnergyConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.File;

@Getter
@Component
public class AbilitySlotsConfig extends HoconConfig {
    @Getter
    private static AbilitySlotsConfig instance;

    @Setting("global.energy")
    private EnergyConfig energy = new EnergyConfig();
    @Setting("global.cooldown")
    private CooldownConfig cooldown = new CooldownConfig();

    public AbilitySlotsConfig() {
        AbilitySlotsConfig.instance = this;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        this.load();
        this.save();
    }

    @Override
    public File getFile() {
        return AbilitySlots.getPlugin().getDataFolder().toPath().resolve("config.conf").toFile();
    }
}
