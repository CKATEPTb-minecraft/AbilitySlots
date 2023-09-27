package dev.ckateptb.minecraft.abilityslots.command.preset.bind;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.config.PresetLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.database.preset.model.AbilityBoardPreset;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@Component
public class PresetBindCommand extends AbilitySlotsSubCommand {
    public PresetBindCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player player = (Player) sender;
        String name = (String) args[0];
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        PresetLanguageConfig config = this.config.getLanguage().getCommand().getPreset();
        Optional<AbilityBoardPreset> optional = user.getPreset(name);
        String reply = "";
        if (optional.isPresent()) {
            user.applyPreset(optional.get());
            reply = config.getBindSuccess();
        } else {
            reply = config.getBindFailed();
        }
        AbilityCommandSender.of(player).sendMessage(reply, "%preset%", name);
    }
}
