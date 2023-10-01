package dev.ckateptb.minecraft.abilityslots.command.preset.list;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.preset.list.config.PresetListConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

@Component
public class PresetListCommand extends AbilitySlotsSubCommand {
    public PresetListCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player player = (Player) sender;
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        PresetListConfig config = this.config.getLanguage().getCommand().getPreset().getList();
        Set<String> presets = user.getPresets();
        AbilityCommandSender commandSender = AbilityCommandSender.of(player);
        if (presets.size() == 0) {
            commandSender.sendMessage(config.getEmpty());
            return;
        }
        String presetFormat = config.getPresetFormat();
        StringBuilder builder = new StringBuilder(config.getReply());
        presets.forEach(preset -> builder.append("\n").append(presetFormat.replaceAll("%preset_name%", preset)));
        commandSender.sendMessage(builder.toString());
    }
}
