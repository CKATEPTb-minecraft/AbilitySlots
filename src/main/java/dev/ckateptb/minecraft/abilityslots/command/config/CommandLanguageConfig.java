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

    private String displayCategories = "§7Abilities from the following categories are available to you (click to view):";
    private String noCategoriesAvailable = "§8-- no categories available --";

    private String displayAbilities = "§7The following abilities are available from the category %category%§r§7:";
    private String displayBindable = "§7Bindable (click to bind):";
    private String displayPassives = "§7Passive:";
    private String displaySequences = "§7Sequence:";
    private String noAbilitiesAvailable = "§8-- no abilities available --";

    private String abilityInstruction = "§8Description:";
    private String abilityDescription = "§8Instruction:";
    private String clickToBind = "Click to bind";

    private String clearAll = "§aYou have unbind all abilities list";
    private String clearSlot = "§aYou have unbind ability from slot §8%slot%";

    private String successfulBind = "§aYou have successfully bound the ability %ability%§r§a to slot §8%slot%";
}
