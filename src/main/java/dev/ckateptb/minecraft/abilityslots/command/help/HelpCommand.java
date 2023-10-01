package dev.ckateptb.minecraft.abilityslots.command.help;

import cloud.commandframework.context.CommandContext;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.help.config.HelpConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class HelpCommand extends AbilitySlotsSubCommand {

    public HelpCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public List<String> suggestion(CommandContext<CommandSender> sender, String input) {
        return Stream.of("help")
                .filter(suggestion -> suggestion.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        HelpConfig config = this.config.getLanguage().getCommand().getHelp();
        String reply = String.join("\n", config.getReply());
        AbilityCommandSender.of(sender).sendMessage(PlaceholderAPI.setPlaceholders(null, reply));
    }
}
