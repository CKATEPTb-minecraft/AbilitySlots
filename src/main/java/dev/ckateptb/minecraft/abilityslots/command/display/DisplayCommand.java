package dev.ckateptb.minecraft.abilityslots.command.display;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.display.config.DisplayConfig;
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
        DisplayConfig display = this.config.getLanguage().getCommand().getDisplay();
        if (category == null) this.displayCategories(sender, display);
        else this.displayAbilities(sender, category, display);
    }

    private void displayCategories(CommandSender sender, DisplayConfig config) {
        String categoryFormat = config.getCategoryFormat();
        String allowedCategories = this.categoryService.getCategories().stream()
                .filter(value -> {
                    if (sender instanceof Player player) {
                        return this.userService.getAbilityUser(player).canUse(value);
                    }
                    return true;
                })
                .sorted(DISPLAY_NAME_COMPARATOR)
                .map(category -> categoryFormat
                        .replaceAll("%category_ability_prefix%", category.getAbilityPrefix())
                        .replaceAll("%category_display_name%", category.getDisplayName())
                        .replaceAll("%category_name%", category.getName())
                )
                .collect(Collectors.joining("\n"));
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        if (allowedCategories.length() > 0) {
            commandSender.sendMessage(String.join("\n", config.getAllowedCategories(), allowedCategories));
        } else commandSender.sendMessage(config.getEmptyCategories());
    }

    private void displayAbilities(CommandSender sender, AbilityCategory category, DisplayConfig config) {
        List<IAbilityDeclaration<?>> bindable = new ArrayList<>();
        List<IAbilityDeclaration<?>> passives = new ArrayList<>();
        List<IAbilityDeclaration<?>> sequences = new ArrayList<>();
        this.abilityService.getDeclarations().stream()
                .filter(declaration -> {
                    if (declaration.getCategory() != category) return false;
                    if (!(sender instanceof Player player)) return true;
                    return this.userService.getAbilityUser(player).canUse(declaration);
                })
                .sorted(DISPLAY_NAME_COMPARATOR)
                .forEach(declaration -> {
                    if (declaration.isBindable()) {
                        bindable.add(declaration);
                    }
                    if (declaration.isActivatedBy(ActivationMethod.SEQUENCE)) {
                        sequences.add(declaration);
                    }
                    if (declaration.isActivatedBy(ActivationMethod.PASSIVE)) {
                        passives.add(declaration);
                    }
                });
        int bindableCount = bindable.size();
        int passiveCount = passives.size();
        int sequenceCount = sequences.size();
        int count = bindableCount + passiveCount + sequenceCount;
        if (count == 0) {
            AbilityCommandSender.of(sender).sendMessage(config.getEmptyAbilities());
            return;
        }
        StringBuilder builder = new StringBuilder(config.getAllowedAbilities());
        if (bindableCount > 0) {
            builder.append("\n").append(config.getBindable());
            String format = config.getBindableFormat();
            bindable.stream()
                    .map(declaration -> this.setPlaceholders(declaration, format))
                    .forEach(ability -> builder.append("\n").append(ability));
        }
        if (passiveCount > 0) {
            builder.append("\n").append(config.getPassive());
            String format = config.getPassiveFormat();
            passives.stream()
                    .map(declaration -> this.setPlaceholders(declaration, format))
                    .forEach(ability -> builder.append("\n").append(ability));
        }
        if (sequenceCount > 0) {
            builder.append("\n").append(config.getSequence());
            String format = config.getSequenceFormat();
            sequences.stream()
                    .map(declaration -> this.setPlaceholders(declaration, format))
                    .forEach(ability -> builder.append("\n").append(ability));
        }
        AbilityCommandSender.of(sender).sendMessage(builder.toString());
    }

    private String setPlaceholders(IAbilityDeclaration<?> declaration, String formatter) {
        return formatter
                .replaceAll("%category_ability_prefix%", declaration.getCategory().getAbilityPrefix())
                .replaceAll("%ability_display_name%", declaration.getDisplayName())
                .replaceAll("%ability_name%", declaration.getName())
                .replaceAll("%ability_author%", declaration.getAuthor())
                .replaceAll("%ability_description%", declaration.getDescription())
                .replaceAll("%ability_instruction%", declaration.getInstruction());
    }
}
