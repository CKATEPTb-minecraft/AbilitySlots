package dev.ckateptb.minecraft.abilityslots.ability.processor.reactive;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import lombok.CustomLog;
import org.slf4j.helpers.MessageFormatter;

@CustomLog
public abstract class AbstractReactiveAbilityProcessor implements ReactiveAbilityProcessor {
    public void destroy(Ability... destroyed) {
        for (Ability ability : destroyed) {
            ability.setLocked(true);
            try {
                ability.destroy(null);
            } catch (Throwable exception) {
                IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                log.error(MessageFormatter.arrayFormat(
                        "There was an error on ability {} was destroyed. Contact the author {}.",
                        new Object[]{declaration.getName(), declaration.getAuthor()}
                ).getMessage(), exception);
            }
        }
    }
}
