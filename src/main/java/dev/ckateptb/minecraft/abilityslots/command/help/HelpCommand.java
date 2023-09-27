package dev.ckateptb.minecraft.abilityslots.command.help;

import cloud.commandframework.context.CommandContext;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.config.CommandLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.config.PresetLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

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
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PresetLanguageConfig preset = config.getPreset();
        PluginDescriptionFile description = this.plugin.getDescription();
        AbilityCommandSender.of(sender).sendMessage("""
                        &6%plugin% &av.%version% &cby &[CKATEPTb](click:url https://github.com/CKATEPTb)(hover:text &7%open_author%)
                        &r&7&[/abilityslots display [category\\]](click:suggest /abilityslots display)(hover:text &7%display%)
                        &r&7&[/abilityslots bind <ability>](click:suggest /abilityslots bind)(hover:text &7%bind%)
                        &r&7&[/abilityslots clear [slot\\]](click:suggest /abilityslots clear)(hover:text &7%clear%)
                        &r&7&[/abilityslots who [player\\]](click:suggest /abilityslots who)(hover:text &7%who%)
                        &r&7&[/abilityslots preset list](click:suggest /abilityslots preset list)(hover:text &7%preset_list%)
                        &r&7&[/abilityslots preset create <name>](click:suggest /abilityslots preset create)(hover:text &7%preset_create%)
                        &r&7&[/abilityslots preset delete <name>](click:suggest /abilityslots preset delete)(hover:text &7%preset_delete%)
                        &r&7&[/abilityslots preset bind <name>](click:suggest /abilityslots preset bind)(hover:text &7%preset_bind%)
                        &r&7&[/abilityslots reload](click:suggest /abilityslots reload)(hover:text &7%reload%)""",
                "%plugin%", description.getName(),
                "%version%", description.getVersion(),
                "%open_author%", config.getOpenAuthor(),
                "%display%", config.getDisplay(),
                "%bind%", config.getBind(),
                "%clear%", config.getClear(),
                "%who%", config.getWho(),
                "%preset_list%", preset.getList(),
                "%preset_create%", preset.getCreate(),
                "%preset_delete%", preset.getDelete(),
                "%preset_bind%", preset.getBind(),
                "%reload%", config.getReload()
        );
    }
}
