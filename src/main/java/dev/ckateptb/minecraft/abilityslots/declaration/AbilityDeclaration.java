package dev.ckateptb.minecraft.abilityslots.declaration;

import de.themoep.minedown.MineDown;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;

import java.util.Set;

public interface AbilityDeclaration {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getFormattedName();

    String getFormattedNameForUser(AbilityUser user);

    String getDescription();

    void setDescription(String description);

    String getInstruction();

    void setInstruction(String instruction);

    long getCooldown();

    void setCooldown(long cooldown);

    double getCost();

    void setCost(double cost);

    boolean isActivatedBy(ActivationMethod method);

    ActivationMethod[] getActivationMethods();

    boolean isBindable();

    String getAuthor();

    AbilityCategory getCategory();

    Ability createAbility();

    Class<? extends Ability> getAbilityClass();

    boolean isCollisionParticipant();

    default boolean canDestroyAbility(Ability ability) {
        return this.canDestroyAbility(ability.getDeclaration());
    }

    boolean canDestroyAbility(AbilityDeclaration declaration);

    Set<AbilityDeclaration> getDestroyAbilities();

    boolean allowDestroyAbility(AbilityDeclaration ability);

    boolean denyDestroyAbility(AbilityDeclaration ability);

    default BaseComponent[] toBaseComponent() {
        AbilitySlotsConfig config = AbilitySlots.config();
        AbilityCategory category = getCategory();
        String prefix = config.isRespectCategoryPrefix() ? category.getPrefix() : "";
        StringBuilder builder = new StringBuilder();
        if (!isActivatedBy(ActivationMethod.PASSIVE) && !isActivatedBy(ActivationMethod.SEQUENCE)) {
            builder
                    .append(config.getBindToSlotText())
                    .append(prefix)
                    .append("[①](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 1)")
                    .append("[②](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 2)")
                    .append("[③](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 3)")
                    .append("[④](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 4)")
                    .append("[⑤](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 5)")
                    .append("[⑥](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 6)")
                    .append("[⑦](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 7)")
                    .append("[⑧](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 8)")
                    .append("[⑨](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 9) - ");
        }
        builder
                .append("[")
                .append(getFormattedName())
                .append("]")
                .append("(")
                .append("hover=")
                .append(ChatColor.RESET)
                .append(config.getAuthorText())
                .append(getAuthor())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getCategoryText())
                .append(prefix)
                .append(category.getDisplayName())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getDescriptionText())
                .append(prefix)
                .append(getDescription())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getInstructionText())
                .append(prefix)
                .append(getInstruction())
                .append(")");
        return MineDown.parse(builder.toString());
    }
}
