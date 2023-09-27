package dev.ckateptb.minecraft.abilityslots.command;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.context.CommandContext;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.bind.BindCommand;
import dev.ckateptb.minecraft.abilityslots.command.clear.ClearCommand;
import dev.ckateptb.minecraft.abilityslots.command.display.DisplayCommand;
import dev.ckateptb.minecraft.abilityslots.command.help.HelpCommand;
import dev.ckateptb.minecraft.abilityslots.command.parser.AbilityParser;
import dev.ckateptb.minecraft.abilityslots.command.parser.CategoryParser;
import dev.ckateptb.minecraft.abilityslots.command.preset.bind.PresetBindCommand;
import dev.ckateptb.minecraft.abilityslots.command.preset.create.PresetCreateCommand;
import dev.ckateptb.minecraft.abilityslots.command.preset.delete.PresetDeleteCommand;
import dev.ckateptb.minecraft.abilityslots.command.preset.list.PresetListCommand;
import dev.ckateptb.minecraft.abilityslots.command.reload.ReloadCommand;
import dev.ckateptb.minecraft.abilityslots.command.who.WhoCommand;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.supervisor.Command;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@Getter
@Component
@RequiredArgsConstructor
public class AbilitySlotsCommand implements Command<AbilitySlots> {

    private final AbilitySlots plugin;

    private final AbilityCategoryService categoryService;
    private final AbilityUserService userService;
    private final AbilityDeclarationService abilityService;

    private final HelpCommand help;
    private final BindCommand bind;
    private final DisplayCommand display;
    private final ReloadCommand reload;
    private final ClearCommand clear;
    private final WhoCommand who;
    private final PresetListCommand presetList;
    private final PresetCreateCommand presetCreate;
    private final PresetDeleteCommand presetDelete;
    private final PresetBindCommand presetBind;

    @Override
    public void parserRegistry(ParserRegistry<CommandSender> registry) {
        registry.registerParserSupplier(TypeToken.get(IAbilityDeclaration.class), options -> new AbilityParser(this.abilityService, this.userService));
        registry.registerParserSupplier(TypeToken.get(AbilityCategory.class), options -> new CategoryParser(this.categoryService, this.userService));
    }

    @Suggestions("help")
    public List<String> suggestionHelp(CommandContext<CommandSender> sender, String input) {
        return this.help.suggestion(sender, input);
    }

    @CommandMethod("abilityslots|as [help]")
    @CommandPermission("abilityslots.command.help")
    public void processHelp(CommandSender sender, @Argument(value = "help", suggestions = "help") String ignore) {
        this.help.process(sender, ignore);

    }

    @SuppressWarnings("all")
    @CommandMethod("abilityslots|as bind|b <ability> [slot]")
    @CommandPermission("abilityslots.command.bind")
    public void processBind(Player sender, @Argument("ability") IAbilityDeclaration ability, @Argument("slot") @Range(min = "1", max = "9") Integer slot) {
        this.bind.process(sender, ability, slot);
    }

    @CommandMethod("abilityslots|as display|d [category]")
    @CommandPermission("abilityslots.command.display")
    public void processDisplay(CommandSender sender, @Argument("category") AbilityCategory category) {
        this.display.process(sender, category);
    }


    @CommandMethod("abilityslots|as reload|r")
    @CommandPermission("abilityslots.command.reload")
    public void processReload(CommandSender sender) {
        this.reload.process(sender);
    }

    @CommandMethod("abilityslots|as clear|c [slot]")
    @CommandPermission("abilityslots.command.clear")
    public void processClear(Player sender, @Argument("slot") @Range(min = "1", max = "9") Integer slot) {
        this.clear.process(sender, slot);
    }

    @CommandMethod("abilityslots|as who|w <target>")
    @CommandPermission("abilityslots.command.who")
    public void processWho(CommandSender sender, @Argument("target") Player player) {
        this.who.process(sender, player);
    }

    @CommandMethod("abilityslots|as preset|p list|l")
    @CommandPermission("abilityslots.command.preset.list")
    public void processPresetList(Player sender) {
        this.presetList.process(sender);
    }

    @CommandMethod("abilityslots|as preset|p create|c <name>")
    @CommandPermission("abilityslots.command.preset.create")
    public void processPresetCreate(Player sender, @Argument("name") String name) {
        this.presetCreate.process(sender, name);
    }

    @Suggestions("preset")
    public List<String> suggestionPreset(CommandContext<CommandSender> context, String input) {
        CommandSender sender = context.getSender();
        if (sender instanceof Player player) {
            return this.userService.getAbilityUser(player).getPresets().stream()
                    .filter(preset -> preset.trim().toLowerCase().startsWith(input.trim().toLowerCase()))
                    .toList();
        } else return Collections.emptyList();
    }

    @CommandMethod("abilityslots|as preset|p delete|d <name>")
    @CommandPermission("abilityslots.command.preset.delete")
    public void processPresetDelete(Player sender, @Argument(value = "name", suggestions = "preset") String name) {
        this.presetDelete.process(sender, name);
    }

    @CommandMethod("abilityslots|as preset|p bind|b <name>")
    @CommandPermission("abilityslots.command.preset.bind")
    public void processPresetBind(Player sender, @Argument(value = "name", suggestions = "preset") String name) {
        this.presetBind.process(sender, name);
    }
}
