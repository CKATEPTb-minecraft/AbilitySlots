package dev.ckateptb.minecraft.abilityslots.command.config;

import lombok.Getter;

@Getter
public class PresetLanguageConfig {
    private String list = "View Specified Player Abilities";
    private String listProcess = "&6List of your presets &8(click to use)&6: &8[%presets%&8]";
    private String listPrefix = "&a";

    private String create = "Save current abilities to preset with specified name";
    private String createSuccess = "&aPreset with name &8%preset% &acreated successful";
    private String createFailed = "&cPreset with name &8%preset% &calready exists";

    private String delete = "Delete the specified preset";
    private String deleteSuccess = "&aPreset with name &8%preset% &adeleted successful";
    private String deleteFailed = "&cPreset with name &8%preset% &cnot found";

    private String bind = "Activate the specified preset";
    private String bindSuccess = "&aPreset with name &8%preset% &abound successful";
    private String bindFailed = "&cPreset with name &8%preset% &cnot found";
}
