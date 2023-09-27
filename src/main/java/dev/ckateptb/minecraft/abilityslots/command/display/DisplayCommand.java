package dev.ckateptb.minecraft.abilityslots.command.display;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.config.CommandLanguageConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.interfaces.DisplayNameHolder;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DisplayCommand extends AbilitySlotsSubCommand {
    private static final Comparator<DisplayNameHolder> DISPLAY_NAME_COMPARATOR = (o1, o2) ->
            ChatColor.stripColor(o1.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getDisplayName()));

    public DisplayCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        AbilityCategory category = (AbilityCategory) args[0];
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
        AbilityCommandSender.of(sender).sendMessage(config.getDisplayAbilities().replaceAll("%category%", categoryInfo) + "\n&r" +
                config.getDisplayBindable() + "&r" +
                (bindableAbilities.length() < 1 ? " " + config.getNoAbilitiesAvailable() : "\n" + bindableAbilities) + "\n&r" +
                config.getDisplayPassives() + "&r" +
                (passiveAbilities.length() < 1 ? " " + config.getNoAbilitiesAvailable() : "\n" + passiveAbilities) + "\n&r" +
                config.getDisplaySequences() + "&r" +
                (sequenceAbilities.length() < 1 ? " " + config.getNoAbilitiesAvailable() : "\n" + sequenceAbilities)
        );
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
        AbilityCommandSender.of(sender).sendMessage(
                config.getDisplayCategories() +
                        "&r " +
                        (allowedCategories.length() > 0 ? allowedCategories : config.getNoCategoriesAvailable())
        );
    }
}
