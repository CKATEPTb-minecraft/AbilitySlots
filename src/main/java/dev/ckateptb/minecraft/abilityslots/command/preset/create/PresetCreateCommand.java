package dev.ckateptb.minecraft.abilityslots.command.preset.create;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.preset.create.config.PresetCreateConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class PresetCreateCommand extends AbilitySlotsSubCommand {
    public PresetCreateCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        String name = (String) args[0];
        PlayerAbilityUser user = this.userService.getAbilityUser((Player) sender);
        PresetCreateConfig create = this.config.getLanguage().getCommand().getPreset().getCreate();
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        if (user.saveAsPreset(name)) {
            commandSender.sendMessage(create.getReply().replaceAll("%preset_name%", name));
        } else {
            commandSender.sendMessage(create.getFailed().replaceAll("%preset_name%", name));
        }
    }
}
