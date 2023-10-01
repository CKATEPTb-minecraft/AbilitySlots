package dev.ckateptb.minecraft.abilityslots.command.toggle;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.command.toggle.config.ToggleConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class ToggleCommand extends AbilitySlotsSubCommand {
    public ToggleCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        PlayerAbilityUser user = this.userService.getAbilityUser((Player) sender);
        ToggleConfig toggle = this.config.getLanguage().getCommand().getToggle();
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        if(user.isAbilitiesEnabled()) {
            user.disableAbilities();
            commandSender.sendMessage(toggle.getDisable());
        } else {
            user.enableAbilities();
            commandSender.sendMessage(toggle.getEnable());
        }
    }
}
