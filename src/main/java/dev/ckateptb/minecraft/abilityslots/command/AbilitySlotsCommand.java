package dev.ckateptb.minecraft.abilityslots.command;

import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.command.config.CommandLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.config.PresetLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.parser.CategoryParser;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import io.leangen.geantyref.TypeToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.ckateptb.minecraft.abilityslots.message.MessageFormatter.toComponent;

@Getter
@Component
@RequiredArgsConstructor
public class AbilitySlotsCommand {
    private final AbilitySlots plugin;
    private final AbilitySlotsConfig config;
    private final AbilityCategoryService categoryService;
    private final AbilityUserService userService;
    private final AbilityDeclarationService abilityService;

    @PostConstruct
    public void init() {
        plugin.executeOnEnable(this::register);
    }

    @SneakyThrows
    private void register() {
        PaperCommandManager<CommandSender> manager = PaperCommandManager.createNative(plugin, CommandExecutionCoordinator.simpleCoordinator());
        manager.parserRegistry().registerParserSupplier(TypeToken.get(AbilityCategory.class),
                options -> new CategoryParser(this.categoryService, this.userService));
        if (manager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            manager.registerBrigadier();
        }
        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }
        Function<ParserParameters, CommandMeta> noDescription = (sender) -> SimpleCommandMeta.builder().with(CommandMeta.DESCRIPTION, "No description").build();
        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class, noDescription);
        (new MinecraftExceptionHandler<CommandSender>())
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler().apply(manager, (sender) -> sender);
        annotationParser.parse(this);
    }

    @ProxiedBy("abilityslots")
    @CommandMethod("abilityslots help")
    @CommandPermission("abilityslots.command.help")
    public void help(CommandSender sender) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PresetLanguageConfig preset = config.getPreset();
        PluginDescriptionFile description = this.plugin.getDescription();
        sender.sendMessage(
                toComponent("""
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
                ));
    }

    @CommandMethod("abilityslots display [category]")
    @CommandPermission("abilityslots.command.display")
    public void display(CommandSender sender, @Argument("category") AbilityCategory category) {
        if (category == null) {
            sender.sendMessage(toComponent(this.categoryService.getCategories().stream()
                    .filter(value -> {
                        if (sender instanceof Player player) {
                            return this.userService.getAbilityUser(player).canUse(value);
                        }
                        return true;
                    })
                    .sorted((o1, o2) -> ChatColor.stripColor(o1.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getDisplayName())))
                    .map(o -> "&[" + o.getDisplayName() + "](click:suggest /abilityslots display " + o.getName() + ")" +
                            "(hover:text " + o.getAbilityPrefix() + o.getDescription() + ")")
                    .collect(Collectors.joining("&r\n"))));
        } else {
            List<IAbilityDeclaration<?>> bindable = new ArrayList<>();
            List<IAbilityDeclaration<?>> passives = new ArrayList<>();
            List<IAbilityDeclaration<?>> sequences = new ArrayList<>();
            abilityService.getDeclarations().stream()
                    .filter(declaration -> declaration.getCategory() == category)
                    .filter(declaration -> {
                        if (sender instanceof Player player) {
                            return this.userService.getAbilityUser(player).canUse(declaration);
                        }
                        return true;
                    })
                    .sorted((o1, o2) -> ChatColor.stripColor(o1.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getDisplayName())))
                    .forEach(declaration -> {
                        int length = declaration.getActivationMethods().length;
                        boolean sequence = declaration.isActivatedBy(ActivationMethod.SEQUENCE);
                        boolean passive = declaration.isActivatedBy(ActivationMethod.PASSIVE);
                        if (sequence) {
                            sequences.add(declaration);
                            if(length > (passive ? 2 : 1)) {
                                bindable.add(declaration);
                            }
                        }
                        if (passive) {
                            passives.add(declaration);
                            if(length > (sequence ? 2 : 1)) {
                                bindable.add(declaration);
                            }
                        }
                        if(!sequence && !passive) {
                            bindable.add(declaration);
                        }
                    });
            // TODO Отображать всю эту херню
        }
    }

    @CommandMethod("abilityslots reload")
    @CommandPermission("abilityslots.command.reload")
    public void reload(CommandSender sender) {
        CommandLanguageConfig config = this.config.getLanguage().getCommand();
        PluginDescriptionFile description = this.plugin.getDescription();
        sender.sendMessage(toComponent(config.getReloadStart()
                .replaceAll("%plugin%", description.getName())
                .replaceAll("%version%", description.getVersion())));
        this.plugin.reload();
    }
}
