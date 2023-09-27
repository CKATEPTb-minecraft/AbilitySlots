package dev.ckateptb.minecraft.abilityslots.command.who;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Component
public class WhoCommand extends AbilitySlotsSubCommand {
    public WhoCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player target = (Player) args[0];
        if (target == null) return;
        String empty = this.config.getGlobal().getBoard().getEmpty();
        PlayerAbilityUser user = this.userService.getAbilityUser(target);
        AbilityCommandSender.of(sender).sendMessage(this.config.getLanguage().getCommand().getWhoPlayer(),
                "%player%", target.getName(),
                "%ability_1%", Optional.ofNullable(user.getAbility(1)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_2%", Optional.ofNullable(user.getAbility(2)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_3%", Optional.ofNullable(user.getAbility(3)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_4%", Optional.ofNullable(user.getAbility(4)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_5%", Optional.ofNullable(user.getAbility(5)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_6%", Optional.ofNullable(user.getAbility(6)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_7%", Optional.ofNullable(user.getAbility(7)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_8%", Optional.ofNullable(user.getAbility(8)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_9%", Optional.ofNullable(user.getAbility(9)).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%prefix_1%", Optional.ofNullable(user.getAbility(1)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_2%", Optional.ofNullable(user.getAbility(2)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_3%", Optional.ofNullable(user.getAbility(3)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_4%", Optional.ofNullable(user.getAbility(4)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_5%", Optional.ofNullable(user.getAbility(5)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_6%", Optional.ofNullable(user.getAbility(6)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_7%", Optional.ofNullable(user.getAbility(7)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_8%", Optional.ofNullable(user.getAbility(8)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%prefix_9%", Optional.ofNullable(user.getAbility(9)).map(ability -> ability.getCategory().getAbilityPrefix()).orElse(""),
                "%description_1%", Optional.ofNullable(user.getAbility(1)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_2%", Optional.ofNullable(user.getAbility(2)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_3%", Optional.ofNullable(user.getAbility(3)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_4%", Optional.ofNullable(user.getAbility(4)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_5%", Optional.ofNullable(user.getAbility(5)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_6%", Optional.ofNullable(user.getAbility(6)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_7%", Optional.ofNullable(user.getAbility(7)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_8%", Optional.ofNullable(user.getAbility(8)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%description_9%", Optional.ofNullable(user.getAbility(9)).map(IAbilityDeclaration::getDescription).orElse("§r§8-"),
                "%instruction_1%", Optional.ofNullable(user.getAbility(1)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_2%", Optional.ofNullable(user.getAbility(2)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_3%", Optional.ofNullable(user.getAbility(3)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_4%", Optional.ofNullable(user.getAbility(4)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_5%", Optional.ofNullable(user.getAbility(5)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_6%", Optional.ofNullable(user.getAbility(6)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_7%", Optional.ofNullable(user.getAbility(7)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_8%", Optional.ofNullable(user.getAbility(8)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-"),
                "%instruction_9%", Optional.ofNullable(user.getAbility(9)).map(IAbilityDeclaration::getInstruction).orElse("§r§8-")
        );
    }
}
