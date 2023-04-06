/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.ckateptb.minecraft.abilityslots.declaration.annotaion;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.util.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class AnnotationBasedAbilityDeclaration implements dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration {
    private final String name;
    private final Class<? extends Ability> abilityClass;
    private final AbilityCategory category;
    private final String author;
    private final ActivationMethod[] activationMethods;
    private final boolean collisionParticipant;
    private final Set<AbilityDeclaration> destroyAbilities = new HashSet<>();
    private long cooldown;
    private double cost;
    private boolean enabled;
    private String displayName;
    private String description;
    private String instruction;
    private boolean bindable;

    public AnnotationBasedAbilityDeclaration(dev.ckateptb.minecraft.abilityslots.declaration.annotaion.AbilityDeclaration abilityInfo, AbilityCategory abilityCategory, Class<? extends Ability> abilityClass) {
        this.name = abilityInfo.name();
        this.abilityClass = abilityClass;
        this.category = abilityCategory;
        this.displayName = abilityInfo.displayName();
        this.description = abilityInfo.description();
        this.instruction = abilityInfo.instruction();
        this.cooldown = abilityInfo.cooldown();
        this.cost = abilityInfo.cost();
        this.enabled = true;
        this.bindable = abilityInfo.bindable();
        this.author = abilityInfo.author();
        this.activationMethods = abilityInfo.activationMethods();
        this.collisionParticipant = abilityClass.isAnnotationPresent(CollisionParticipant.class);
    }

    @Override
    public boolean isActivatedBy(ActivationMethod method) {
        return Arrays.stream(this.activationMethods).anyMatch(activationMethod -> activationMethod.equals(method));
    }

    @Override
    public String getFormattedName() {
        return getFormattedNameForUser(null);
    }

    @Override
    public String getFormattedNameForUser(AbilityUser user) {
        StringBuilder builder = new StringBuilder();
        String prefix = this.category.getPrefix();
        builder.append(prefix);
        if (user != null && user.hasCooldown(this)) {
            long cooldown = user.getCooldowns().get(this) - System.currentTimeMillis();
            return builder
                    .append(ChatColor.STRIKETHROUGH)
                    .append(this.getDisplayName())
                    .append(ChatColor.RESET)
                    .append(prefix)
                    .append(" - ")
                    .append(TimeUtil.formatTime(cooldown))
                    .toString();
        }
        return builder.append(this.displayName).toString();
    }

    @Override
    @SneakyThrows
    public Ability createAbility() {
        return abilityClass.getConstructor().newInstance();
    }

    @Override
    public boolean canDestroyAbility(AbilityDeclaration ability) {
        return this.destroyAbilities.contains(ability);
    }

    @Override
    public Set<AbilityDeclaration> getDestroyAbilities() {
        return Collections.unmodifiableSet(this.destroyAbilities);
    }

    @Override
    public boolean allowDestroyAbility(AbilityDeclaration ability) {
        return this.destroyAbilities.add(ability);
    }

    @Override
    public boolean denyDestroyAbility(AbilityDeclaration ability) {
        return this.destroyAbilities.remove(ability);
    }
}
