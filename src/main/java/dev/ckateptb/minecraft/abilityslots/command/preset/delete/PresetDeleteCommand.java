package dev.ckateptb.minecraft.abilityslots.command.preset.delete;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.preset.delete.config.PresetDeleteConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.database.preset.model.AbilityBoardPreset;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Component
public class PresetDeleteCommand extends AbilitySlotsSubCommand {
    public PresetDeleteCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        String name = (String) args[0];
        PlayerAbilityUser user = this.userService.getAbilityUser((Player) sender);
        PresetDeleteConfig config = this.config.getLanguage().getCommand().getPreset().getDelete();
        Optional<AbilityBoardPreset> optional = user.getPreset(name);
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        if (optional.isPresent()) {
            user.deletePreset(optional.get());
            commandSender.sendMessage(config.getReply().replaceAll("%preset_name%", name));
        } else {
            commandSender.sendMessage(config.getFailed().replaceAll("%preset_name%", name));
        }
    }
}
