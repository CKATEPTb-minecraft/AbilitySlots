package dev.ckateptb.minecraft.abilityslots.command.clear;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.config.CommandLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class ClearCommand extends AbilitySlotsSubCommand {
    public ClearCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player player = (Player) sender;
        Integer slot = (Integer) args[0];
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        if (slot == null) {
            for (int i = 1; i <= 9; ++i) {
                user.setAbility(i, null, false);
            }
            user.saveCurrentBoard();
            AbilityCommandSender.of(sender).sendMessage(config.getClearAll());
        } else {
            user.setAbility(slot, null);
            AbilityCommandSender.of(sender).sendMessage(config.getClearSlot().replaceAll("%slot%", String.valueOf(slot)));
        }
    }
}
