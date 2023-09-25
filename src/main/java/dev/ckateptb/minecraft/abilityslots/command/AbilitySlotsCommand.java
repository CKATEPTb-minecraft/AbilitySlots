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
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.command.config.CommandLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.config.PresetLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.parser.AbilityParser;
import dev.ckateptb.minecraft.abilityslots.command.parser.CategoryParser;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.interfaces.DisplayNameHolder;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.supervisor.Command;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.ckateptb.minecraft.abilityslots.message.MessageFormatter.toComponent;

@Getter
@Component
@RequiredArgsConstructor
public class AbilitySlotsCommand implements Command<AbilitySlots> {
    private static final Comparator<DisplayNameHolder> DISPLAY_NAME_COMPARATOR = (o1, o2) ->
            ChatColor.stripColor(o1.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getDisplayName()));

    private final AbilitySlots plugin;
    private final AbilitySlotsConfig config;
    private final AbilityCategoryService categoryService;
    private final AbilityUserService userService;
    private final AbilityDeclarationService abilityService;

    private final String help = """
            &6%plugin% &av.%version% &cby &[CKATEPTb](click:url https://github.com/CKATEPTb)(hover:text &7%open_author%)
            &r&7&[/abilityslots display [category\\]](click:suggest /abilityslots display)(hover:text &7%display%)
            &r&7&[/abilityslots bind <ability>](click:suggest /abilityslots bind)(hover:text &7%bind%)
            &r&7&[/abilityslots clear [slot\\]](click:suggest /abilityslots clear)(hover:text &7%clear%)
            &r&7&[/abilityslots who [player\\]](click:suggest /abilityslots who)(hover:text &7%who%)
            &r&7&[/abilityslots preset list](click:suggest /abilityslots preset list)(hover:text &7%preset_list%)
            &r&7&[/abilityslots preset create <name>](click:suggest /abilityslots preset create)(hover:text &7%preset_create%)
            &r&7&[/abilityslots preset delete <name>](click:suggest /abilityslots preset delete)(hover:text &7%preset_delete%)
            &r&7&[/abilityslots preset bind <name>](click:suggest /abilityslots preset bind)(hover:text &7%preset_bind%)
            &r&7&[/abilityslots reload](click:suggest /abilityslots reload)(hover:text &7%reload%)""";

    @Override
    public void parserRegistry(ParserRegistry<CommandSender> registry) {
        registry.registerParserSupplier(TypeToken.get(IAbilityDeclaration.class), options -> new AbilityParser(this.abilityService, this.userService));
        registry.registerParserSupplier(TypeToken.get(AbilityCategory.class), options -> new CategoryParser(this.categoryService, this.userService));
    }

    @Suggestions("help")
    public List<String> suggestionHelp(CommandContext<CommandSender> sender, String input) {
        return Stream.of("help")
                .filter(suggestion -> suggestion.toLowerCase().startsWith(input.toLowerCase()))
                .collect(Collectors.toList());
    }

    @CommandMethod("abilityslots|as [help]")
    @CommandPermission("abilityslots.command.help")
    public void help(CommandSender sender, @Argument(value = "help", suggestions = "help") String help) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PresetLanguageConfig preset = config.getPreset();
        PluginDescriptionFile description = this.plugin.getDescription();
        sender.sendMessage(toComponent(this.help,
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
        ));
    }

    @CommandMethod("abilityslots|as bind|b <ability> [slot]")
    @CommandPermission("abilityslots.command.bind")
    @SuppressWarnings("all")
    public void bind(Player sender, @Argument("ability") IAbilityDeclaration ability, @Argument("slot") @Range(min = "1", max = "9") Integer slot) {
        System.out.println(sender.getUniqueId());
        PlayerAbilityUser user = this.userService.getAbilityUser(sender);
        if (slot == null) slot = user.getInventory().getHeldItemSlot() + 1;
        user.setAbility(slot, ability);
        sender.sendMessage(toComponent(this.getConfig().getLanguage().getCommand().getSuccessfulBind()
                .replaceAll("%ability%", ability.getDisplayName())
                .replaceAll("%slot%", String.valueOf(slot))));
    }

    @CommandMethod("abilityslots|as display|d [category]")
    @CommandPermission("abilityslots.command.display")
    public void display(CommandSender sender, @Argument("category") AbilityCategory category) {
        if (category == null) {
            this.displayCategories(sender);
        } else {
            this.displayAbilities(sender, category);
        }
    }

    private void displayAbilities(CommandSender sender, AbilityCategory category) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        List<IAbilityDeclaration<?>> bindable = new ArrayList<>();
        List<IAbilityDeclaration<?>> passives = new ArrayList<>();
        List<IAbilityDeclaration<?>> sequences = new ArrayList<>();
        this.abilityService.getDeclarations().stream()
                .filter(declaration -> declaration.getCategory() == category)
                .filter(declaration -> {
                    if (sender instanceof Player player) {
                        return this.userService.getAbilityUser(player).canUse(declaration);
                    }
                    return true;
                })
                .sorted(DISPLAY_NAME_COMPARATOR)
                .forEach(declaration -> {
                    int length = declaration.getActivationMethods().length;
                    boolean sequence = declaration.isActivatedBy(ActivationMethod.SEQUENCE);
                    boolean passive = declaration.isActivatedBy(ActivationMethod.PASSIVE);
                    if (sequence) {
                        sequences.add(declaration);
                        if (length > (passive ? 2 : 1)) {
                            bindable.add(declaration);
                        }
                    }
                    if (passive) {
                        passives.add(declaration);
                        if (length > (sequence ? 2 : 1)) {
                            bindable.add(declaration);
                        }
                    }
                    if (!sequence && !passive) {
                        bindable.add(declaration);
                    }
                });
        String abilityPrefix = category.getAbilityPrefix();
        String abilityDescription = config.getAbilityDescription();
        String abilityInstruction = config.getAbilityInstruction();
        String clickToBind = "(hover:text &r" + abilityPrefix + config.getClickToBind() + ")";
        String bindableAbilities = bindable.stream().map(declaration -> {
            String abilityName = declaration.getName();
            String abilityInfo = "&8 -- &r" + abilityPrefix + "&[" + declaration.getDisplayName() +
                    "](click:suggest /abilityslots bind " + abilityName + ")(hover:text &r" + abilityDescription +
                    " &r" + abilityPrefix + declaration.getDescription() + "\n&r" + abilityInstruction + " &r" +
                    abilityPrefix + declaration.getInstruction() + ")";
            return abilityInfo +
                    " &[①]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 1)" +
                    " &[②]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 2)" +
                    " &[③]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 3)" +
                    " &[④]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 4)" +
                    " &[⑤]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 5)" +
                    " &[⑥]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 6)" +
                    " &[⑦]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 7)" +
                    " &[⑧]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 8)" +
                    " &[⑨]" + clickToBind + "(click:suggest /abilityslots bind " + declaration.getName() + " 9)";
        }).collect(Collectors.joining("\n&r"));
        Function<? super IAbilityDeclaration<?>, String> mapper = declaration -> "&8 -- &r" +
                abilityPrefix + "&[" + declaration.getDisplayName() + "](hover:text &r" + abilityDescription + " &r" +
                abilityPrefix + declaration.getDescription() + "\n&r" + abilityInstruction + " &r" + abilityPrefix +
                declaration.getInstruction() + ")";
        String passiveAbilities = passives.stream().map(mapper).collect(Collectors.joining("\n&r"));
        String sequenceAbilities = sequences.stream().map(mapper).collect(Collectors.joining("\n&r"));
        String categoryInfo = abilityPrefix + "&[" + category.getDisplayName() +
                "](click:suggest /abilityslots display " + category.getName() + ")(hover:text " + abilityPrefix +
                category.getDescription() + ")";
        sender.sendMessage(toComponent(config.getDisplayAbilities().replaceAll("%category%", categoryInfo) + "\n&r" +
                config.getDisplayBindable() + "&r" +
                (bindableAbilities.length() < 1 ? " " + config.getNoAbilitiesAvailable() : "\n" + bindableAbilities) + "\n&r" +
                config.getDisplayPassives() + "&r" +
                (passiveAbilities.length() < 1 ? " " + config.getNoAbilitiesAvailable() : "\n" + passiveAbilities) + "\n&r" +
                config.getDisplaySequences() + "&r" +
                (sequenceAbilities.length() < 1 ? " " + config.getNoAbilitiesAvailable() : "\n" + sequenceAbilities)
        ));
    }

    private void displayCategories(CommandSender sender) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        String allowedCategories = this.categoryService.getCategories().stream()
                .filter(value -> {
                    if (sender instanceof Player player) {
                        return this.userService.getAbilityUser(player).canUse(value);
                    }
                    return true;
                })
                .sorted(DISPLAY_NAME_COMPARATOR)
                .map(o -> "&[" + o.getDisplayName() + "](click:suggest /abilityslots display " + o.getName() + ")" +
                        "(hover:text " + o.getAbilityPrefix() + o.getDescription() + ")")
                .collect(Collectors.joining("&r, "));
        sender.sendMessage(toComponent(
                config.getDisplayCategories() +
                        "&r " +
                        (allowedCategories.length() > 0 ? allowedCategories : config.getNoCategoriesAvailable())
        ));
    }

    @CommandMethod("abilityslots|as reload|r")
    @CommandPermission("abilityslots.command.reload")
    public void reload(CommandSender sender) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PluginDescriptionFile description = this.plugin.getDescription();
        sender.sendMessage(toComponent(config.getReloadStart()
                .replaceAll("%plugin%", description.getName())
                .replaceAll("%version%", description.getVersion())));
        this.plugin.reload();
    }

    @CommandMethod("abilityslots|as clear|c [slot]")
    @CommandPermission("abilityslots.command.clear")
    public void clear(Player sender, @Argument("slot") @Range(min = "1", max = "9") Integer slot) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PlayerAbilityUser user = this.userService.getAbilityUser(sender);
        if (slot == null) {
            for (int i = 1; i <= 9; ++i) {
                user.setAbility(i, null, false);
            }
            user.saveCurrentBoard();
            sender.sendMessage(toComponent(config.getClearAll()));
        } else {
            user.setAbility(slot, null);
            sender.sendMessage(toComponent(config.getClearSlot().replaceAll("%slot%", String.valueOf(slot))));
        }
    }
}
