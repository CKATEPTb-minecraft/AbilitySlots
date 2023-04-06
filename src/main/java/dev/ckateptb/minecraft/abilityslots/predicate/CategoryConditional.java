
package dev.ckateptb.minecraft.abilityslots.predicate;

import dev.ckateptb.minecraft.abilityslots.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

public interface CategoryConditional extends Conditional<AbilityCategory> {
    CategoryConditional HAS_CATEGORY_PERMISSION = (user, category) -> category != null && user.hasPermission(String.format("abilityslots.abilities.%s", category.getName()).toLowerCase());

    @NoArgsConstructor
    class Builder {
        private final Set<CategoryConditional> conditionals = new HashSet<>();

        public Builder hasPermission() {
            conditionals.add(HAS_CATEGORY_PERMISSION);
            return this;
        }

        public Builder custom(CategoryConditional conditional) {
            conditionals.add(conditional);
            return this;
        }

        public CategoryConditional build() {
            return (user, ability) -> conditionals.stream().allMatch(conditional -> conditional.matches(user, ability));
        }
    }

    @Override
    boolean matches(AbilityUser user, AbilityCategory ability);
}
