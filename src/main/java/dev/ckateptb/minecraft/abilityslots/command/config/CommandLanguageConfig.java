package dev.ckateptb.minecraft.abilityslots.command.config;

import dev.ckateptb.minecraft.abilityslots.command.bind.config.BindConfig;
import dev.ckateptb.minecraft.abilityslots.command.clear.config.ClearConfig;
import dev.ckateptb.minecraft.abilityslots.command.display.config.DisplayConfig;
import dev.ckateptb.minecraft.abilityslots.command.help.config.HelpConfig;
import dev.ckateptb.minecraft.abilityslots.command.preset.config.PresetConfig;
import dev.ckateptb.minecraft.abilityslots.command.reload.config.ReloadConfig;
import dev.ckateptb.minecraft.abilityslots.command.toggle.config.ToggleConfig;
import dev.ckateptb.minecraft.abilityslots.command.who.config.WhoConfig;
import lombok.Getter;

@Getter
public class CommandLanguageConfig {
    private HelpConfig help = new HelpConfig();
    private DisplayConfig display = new DisplayConfig();
    private BindConfig bind = new BindConfig();
    private ClearConfig clear = new ClearConfig();
    private WhoConfig who = new WhoConfig();
    private ToggleConfig toggle = new ToggleConfig();
    private PresetConfig preset = new PresetConfig();
    private ReloadConfig reload = new ReloadConfig();
}
