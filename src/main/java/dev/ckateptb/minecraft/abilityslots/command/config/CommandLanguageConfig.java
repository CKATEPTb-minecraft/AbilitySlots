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

    private String whoPlayer = """
                        &aList of player abilities &6%player%&r&a:&r
                        &[%ability_1%](hover:text &r&8Description: &r%prefix_1%%description_1%
                        &r&8Instruction: &r%prefix_1%%instruction_1%)
                        &[%ability_2%](hover:text &r&8Description: &r%prefix_2%%description_2%
                        &r&8Instruction: &r%prefix_2%%instruction_2%)
                        &[%ability_3%](hover:text &r&8Description: &r%prefix_3%%description_3%
                        &r&8Instruction: &r%prefix_3%%instruction_3%)
                        &[%ability_4%](hover:text &r&8Description: &r%prefix_4%%description_4%
                        &r&8Instruction: &r%prefix_4%%instruction_4%)
                        &[%ability_5%](hover:text &r&8Description: &r%prefix_5%%description_5%
                        &r&8Instruction: &r%prefix_5%%instruction_5%)
                        &[%ability_6%](hover:text &r&8Description: &r%prefix_6%%description_6%
                        &r&8Instruction: &r%prefix_6%%instruction_6%)
                        &[%ability_7%](hover:text &r&8Description: &r%prefix_7%%description_7%
                        &r&8Instruction: &r%prefix_7%%instruction_7%)
                        &[%ability_8%](hover:text &r&8Description: &r%prefix_8%%description_8%
                        &r&8Instruction: &r%prefix_8%%instruction_8%)
                        &[%ability_9%](hover:text &r&8Description: &r%prefix_9%%description_9%
                        &r&8Instruction: &r%prefix_9%%instruction_9%)""";
}
