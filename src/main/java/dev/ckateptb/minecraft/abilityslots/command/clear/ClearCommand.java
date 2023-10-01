package dev.ckateptb.minecraft.abilityslots.command.clear;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.clear.config.ClearConfig;
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
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        ClearConfig config = this.config.getLanguage().getCommand().getClear();
        if (slot == null) {
            for (int i = 1; i <= 9; ++i) {
                user.setAbility(i, null, false);
            }
            user.saveCurrentBoard();
            commandSender.sendMessage(config.getReply());
        } else {
            user.setAbility(slot, null);
            commandSender.sendMessage(config.getReplySlot().replaceAll("%slot%", String.valueOf(slot)));
        }
    }
}
