package dev.ckateptb.minecraft.abilityslots.command.config;

import lombok.Getter;

@Getter
public class CommandLanguageConfig {
    private String display = "Show available abilities (in specified category)";
    private String bind = "Bind the specified ability to the specified slot (if no slot is specified, it will be selected automatically)";
    private String clear = "Unbind the ability from the specified slot (if no slot is specified, it will unbind all)";
    private String who = "Display player abilities";
    private PresetLanguageConfig preset = new PresetLanguageConfig();
    private String reload = "Reload plugin config and abilities";
    private String reloadStart = "§aPlugin &6%plugin% §ais being reloaded...";
    private String openAuthor = "Open author github page";
}
