package dev.ckateptb.minecraft.abilityslots.command.display.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;


@Getter
public class DisplayConfig {
    private String emptyCategories = "&bNo categories found for you";
    private String allowedCategories = "&bThe following categories are available to you:";
    @Comment("Allowed placeholders: %category_ability_prefix%, %category_display_name%, %category_name%")
    private String categoryFormat = "&8↳ &[%category_display_name%&]" +
            "&(hover:text &7Click to view the abilities available to you from this category&)" +
            "&(click:suggest /abilityslots display %category_name%&)";


    private String emptyAbilities = "&bNo abilities found for you";
    private String allowedAbilities = "&bThe following abilities are available to you:";
    private String bindable = "&8↳ &b&o&[Bindable&]&(hover:text &7Abilities you can bind to a slot&):";
    @Comment("Allowed placeholders: %category_ability_prefix%, %ability_display_name%, %ability_name%, " +
            "%ability_author%, %ability_description%, %ability_instruction%")
    private String bindableFormat = " &8↳ &[%category_ability_prefix%%ability_display_name%&]" +
            "&(hover:text " +
            "&8Author: %category_ability_prefix%%ability_author%\n" +
            "&8Description: %category_ability_prefix%%ability_description%\n" +
            "&8Instruction: %category_ability_prefix%%ability_instruction%" +
            "&) &8-" +
            " &[%category_ability_prefix%①&]&(hover:text &7Click to bind the ability to 1 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 1&)" +
            " &[%category_ability_prefix%②&]&(hover:text &7Click to bind the ability to 2 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 2&)" +
            " &[%category_ability_prefix%③&]&(hover:text &7Click to bind the ability to 3 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 3&)" +
            " &[%category_ability_prefix%④&]&(hover:text &7Click to bind the ability to 4 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 4&)" +
            " &[%category_ability_prefix%⑤&]&(hover:text &7Click to bind the ability to 5 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 5&)" +
            " &[%category_ability_prefix%⑥&]&(hover:text &7Click to bind the ability to 6 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 6&)" +
            " &[%category_ability_prefix%⑦&]&(hover:text &7Click to bind the ability to 7 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 7&)" +
            " &[%category_ability_prefix%⑧&]&(hover:text &7Click to bind the ability to 8 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 8&)" +
            " &[%category_ability_prefix%⑨&]&(hover:text &7Click to bind the ability to 9 slot&)" +
            "&(click:suggest /abilityslots bind %ability_name% 9&)";
    private String sequence = "&8↳ &b&o&[Sequence&]&(hover:text &7Abilities created by combinations&):";
    @Comment("Allowed placeholders: %category_ability_prefix%, %ability_display_name%, %ability_name%, " +
            "%ability_author%, %ability_description%, %ability_instruction%")
    private String sequenceFormat = """
             &8↳ &[%category_ability_prefix%%ability_display_name%&]&(hover:text &8Author: %category_ability_prefix%%ability_author%
            &8Description: %category_ability_prefix%%ability_description%
            &8Instruction: %category_ability_prefix%%ability_instruction%&)""";
    private String passive = "&8↳ &b&o&[Passive&]&(hover:text &7Abilities created passively&):";
    @Comment("Allowed placeholders: %category_ability_prefix%, %ability_display_name%, %ability_name%, " +
            "%ability_author%, %ability_description%, %ability_instruction%")
    private String passiveFormat = """
             &8↳ &[%category_ability_prefix%%ability_display_name%&]&(hover:text &8Author: %category_ability_prefix%%ability_author%
            &8Description: %category_ability_prefix%%ability_description%
            &8Instruction: %category_ability_prefix%%ability_instruction%&)""";
}
