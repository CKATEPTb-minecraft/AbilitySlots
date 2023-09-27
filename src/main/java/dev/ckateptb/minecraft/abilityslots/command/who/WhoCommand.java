package dev.ckateptb.minecraft.abilityslots.command.who;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
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
import java.util.function.Function;

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

        IAbilityDeclaration<? extends Ability> ability1 = user.getAbility(1);
        IAbilityDeclaration<? extends Ability> ability2 = user.getAbility(2);
        IAbilityDeclaration<? extends Ability> ability3 = user.getAbility(3);
        IAbilityDeclaration<? extends Ability> ability4 = user.getAbility(4);
        IAbilityDeclaration<? extends Ability> ability5 = user.getAbility(5);
        IAbilityDeclaration<? extends Ability> ability6 = user.getAbility(6);
        IAbilityDeclaration<? extends Ability> ability7 = user.getAbility(7);
        IAbilityDeclaration<? extends Ability> ability8 = user.getAbility(8);
        IAbilityDeclaration<? extends Ability> ability9 = user.getAbility(9);

        Function<IAbilityDeclaration<?>, String> prefixGetter = ability -> ability.getCategory().getAbilityPrefix();

        String emptyHover = "§r§8-";

        AbilityCommandSender.of(sender).sendMessage(this.config.getLanguage().getCommand().getWhoPlayer(),
                "%player%", target.getName(),

                "%ability_1%", Optional.ofNullable(ability1).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_2%", Optional.ofNullable(ability2).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_3%", Optional.ofNullable(ability3).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_4%", Optional.ofNullable(ability4).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_5%", Optional.ofNullable(ability5).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_6%", Optional.ofNullable(ability6).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_7%", Optional.ofNullable(ability7).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_8%", Optional.ofNullable(ability8).map(IAbilityDeclaration::getFormattedName).orElse(empty),
                "%ability_9%", Optional.ofNullable(ability9).map(IAbilityDeclaration::getFormattedName).orElse(empty),

                "%prefix_1%", Optional.ofNullable(ability1).map(prefixGetter).orElse(""),
                "%prefix_2%", Optional.ofNullable(ability2).map(prefixGetter).orElse(""),
                "%prefix_3%", Optional.ofNullable(ability3).map(prefixGetter).orElse(""),
                "%prefix_4%", Optional.ofNullable(ability4).map(prefixGetter).orElse(""),
                "%prefix_5%", Optional.ofNullable(ability5).map(prefixGetter).orElse(""),
                "%prefix_6%", Optional.ofNullable(ability6).map(prefixGetter).orElse(""),
                "%prefix_7%", Optional.ofNullable(ability7).map(prefixGetter).orElse(""),
                "%prefix_8%", Optional.ofNullable(ability8).map(prefixGetter).orElse(""),
                "%prefix_9%", Optional.ofNullable(ability9).map(prefixGetter).orElse(""),

                "%description_1%", Optional.ofNullable(ability1).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_2%", Optional.ofNullable(ability2).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_3%", Optional.ofNullable(ability3).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_4%", Optional.ofNullable(ability4).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_5%", Optional.ofNullable(ability5).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_6%", Optional.ofNullable(ability6).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_7%", Optional.ofNullable(ability7).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_8%", Optional.ofNullable(ability8).map(IAbilityDeclaration::getDescription).orElse(emptyHover),
                "%description_9%", Optional.ofNullable(ability9).map(IAbilityDeclaration::getDescription).orElse(emptyHover),

                "%instruction_1%", Optional.ofNullable(ability1).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_2%", Optional.ofNullable(ability2).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_3%", Optional.ofNullable(ability3).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_4%", Optional.ofNullable(ability4).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_5%", Optional.ofNullable(ability5).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_6%", Optional.ofNullable(ability6).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_7%", Optional.ofNullable(ability7).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_8%", Optional.ofNullable(ability8).map(IAbilityDeclaration::getInstruction).orElse(emptyHover),
                "%instruction_9%", Optional.ofNullable(ability9).map(IAbilityDeclaration::getInstruction).orElse(emptyHover)
        );
    }
}
