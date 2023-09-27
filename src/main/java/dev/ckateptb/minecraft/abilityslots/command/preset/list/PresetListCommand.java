package dev.ckateptb.minecraft.abilityslots.command.preset.list;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.config.PresetLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@Component
public class PresetListCommand extends AbilitySlotsSubCommand {
    public PresetListCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player player = (Player) sender;
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        PresetLanguageConfig config = this.config.getLanguage().getCommand().getPreset();
        String reply = config.getListProcess();
        String prefix = config.getListPrefix();
        AbilityCommandSender.of(player).sendMessage(reply.replaceAll("%presets%", user.getPresets().stream()
                .map(preset -> prefix + "&[" + preset + "](click:suggest /abilityslots preset bind " + preset + ")")
                .collect(Collectors.joining("&r&8, &r"))));
    }
}
