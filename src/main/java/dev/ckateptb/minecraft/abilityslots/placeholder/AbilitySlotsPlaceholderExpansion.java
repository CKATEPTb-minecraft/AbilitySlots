package dev.ckateptb.minecraft.abilityslots.placeholder;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

@Component
public class AbilitySlotsPlaceholderExpansion extends PlaceholderExpansion {
    private final PluginDescriptionFile description;
    private final AbilityUserService userService;

    public AbilitySlotsPlaceholderExpansion(AbilitySlots plugin, AbilityUserService userService) {
        this.description = plugin.getDescription();
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.description.getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return this.description.getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return this.description.getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return null;
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        return switch (params.toLowerCase()) {
            case "ability_1" -> this.getAbility(user, 1, false);
            case "ability_2" -> this.getAbility(user, 2, false);
            case "ability_3" -> this.getAbility(user, 3, false);
            case "ability_4" -> this.getAbility(user, 4, false);
            case "ability_5" -> this.getAbility(user, 5, false);
            case "ability_6" -> this.getAbility(user, 6, false);
            case "ability_7" -> this.getAbility(user, 7, false);
            case "ability_8" -> this.getAbility(user, 8, false);
            case "ability_9" -> this.getAbility(user, 9, false);
            case "ability_current" -> this.getAbility(user, 0, false);
            case "formatted_ability_1" -> this.getAbility(user, 1, true);
            case "formatted_ability_2" -> this.getAbility(user, 2, true);
            case "formatted_ability_3" -> this.getAbility(user, 3, true);
            case "formatted_ability_4" -> this.getAbility(user, 4, true);
            case "formatted_ability_5" -> this.getAbility(user, 5, true);
            case "formatted_ability_6" -> this.getAbility(user, 6, true);
            case "formatted_ability_7" -> this.getAbility(user, 7, true);
            case "formatted_ability_8" -> this.getAbility(user, 8, true);
            case "formatted_ability_9" -> this.getAbility(user, 9, true);
            case "formatted_ability_current" -> this.getAbility(user, 0, true);
            default -> null;
        };
    }

    private String getAbility(PlayerAbilityUser user, Integer slot, boolean formatted) {
        IAbilityDeclaration<? extends Ability> ability;
        if (slot == 0) {
            ability = user.getSelectedAbility();
        } else {
            ability = user.getAbility(slot);
        }
        if (ability == null) {
            return formatted ? this.userService.getConfig().getGlobal().getBoard().getEmpty() + ChatColor.RESET : null;
        }
        if (!formatted) return ability.getDisplayName();
        else return ability.getFormattedName(user);
    }
}
