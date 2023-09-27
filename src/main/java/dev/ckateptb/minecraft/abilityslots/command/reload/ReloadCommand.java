package dev.ckateptb.minecraft.abilityslots.command.reload;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.config.CommandLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

@Component
public class ReloadCommand extends AbilitySlotsSubCommand {
    public ReloadCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PluginDescriptionFile description = this.plugin.getDescription();
        AbilityCommandSender.of(sender).sendMessage(config.getReloadStart()
                .replaceAll("%plugin%", description.getName())
                .replaceAll("%version%", description.getVersion())
        );
        this.plugin.reload();
    }
}
