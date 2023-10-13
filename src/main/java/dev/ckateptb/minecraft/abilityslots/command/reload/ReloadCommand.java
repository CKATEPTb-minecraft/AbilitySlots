package dev.ckateptb.minecraft.abilityslots.command.reload;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.reload.config.ReloadConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class ReloadCommand extends AbilitySlotsSubCommand {
    public ReloadCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player target = (Player) args[0];
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        ReloadConfig config = this.config.getLanguage().getCommand().getReload();
        if (target == null) {
            this.plugin.reload();
            commandSender.sendMessage(PlaceholderAPI.setPlaceholders(null, config.getReply()));
        } else {
            this.userService.reloadAbilityUser(this, target);
            commandSender.sendMessage(config.getReplyPlayer().replaceAll("%player_name%", target.getName()));
        }
    }
}
