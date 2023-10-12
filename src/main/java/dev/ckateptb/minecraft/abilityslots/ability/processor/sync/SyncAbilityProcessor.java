package dev.ckateptb.minecraft.abilityslots.ability.processor.sync;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.processor.AbstractAbilityProcessor;
import lombok.CustomLog;
import org.slf4j.helpers.MessageFormatter;

@CustomLog
public class SyncAbilityProcessor extends AbstractAbilityProcessor {

    public SyncAbilityProcessor(AbilityCollisionService collisionService) {
        super(collisionService);
    }

    @Override
    public void tick() {
        this.abilities.removeIf(ability -> {
            ability.setLocked(true);
            IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
            String name = declaration.getName();
            String author = declaration.getAuthor();
            try {
                if (ability.tick() == AbilityTickStatus.CONTINUE) {
                    return false;
                }
            } catch (Throwable throwable) {
                log.error(MessageFormatter.arrayFormat(
                        "There was an error processing ability {} and has" +
                                " been called back. Contact the author {}.",
                        new Object[]{name, author}
                ).getMessage(), throwable);
            }
            ability.setLocked(false);
            try {
                ability.destroy(null);
            } catch (Throwable exception) {
                log.error(MessageFormatter.arrayFormat(
                        "There was an error on ability {} was destroyed. Contact the author {}.",
                        new Object[]{name, author}
                ).getMessage(), exception);
            }
            return true;
        });
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
