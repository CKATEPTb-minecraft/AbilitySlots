package dev.ckateptb.minecraft.abilityslots.ability.processor;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@CustomLog
@RequiredArgsConstructor
public abstract class AbstractAbilityProcessor implements AbilityProcessor {
    protected final AbilityCollisionService collisionService;
    protected final List<Ability> abilities = Collections.synchronizedList(new ArrayList<>());

    public void register(Ability ability) {
        this.abilities.add(ability);
    }

    @Override
    public void process() {
        if (this.abilities.isEmpty()) return;
        this.collisionService.findCollided(this.abilities).subscribe(this::destroy);
        this.tick();
    }

    public abstract void tick();

    public void destroy(Ability... destroyed) {
        for (Ability ability : destroyed) {
            ability.setLocked(true);
            if (this.abilities.remove(ability)) {
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

    @Override
    public void destroyAll() {
        this.destroy(this.abilities.toArray(Ability[]::new));
    }

    public synchronized Stream<Ability> instances() {
        return new ArrayList<>(this.abilities).stream();
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public int getRate() {
        return 1;
    }
}
