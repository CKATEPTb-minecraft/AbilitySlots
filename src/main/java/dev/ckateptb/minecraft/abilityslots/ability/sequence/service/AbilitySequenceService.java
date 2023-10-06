package dev.ckateptb.minecraft.abilityslots.ability.sequence.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.Sequence;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Component
public class AbilitySequenceService implements Listener {
    private final Map<IAbilityDeclaration<? extends Ability>, List<AbilityAction>> sequences = new HashMap<>();
    private final AtomicInteger maxActionsSize = new AtomicInteger(0);

    public void registerSequence(IAbilityDeclaration<?> declaration, Sequence sequence) {
        List<AbilityAction> actions = Stream.of(sequence.value())
                .map(action -> this.createAction(action.ability(), action.action()))
                .toList();
        this.maxActionsSize.updateAndGet(previous -> (int) FastMath.max(previous, actions.size()));
        this.sequences.put(declaration, actions);
    }

    public synchronized Optional<IAbilityDeclaration<? extends Ability>> findSequence(List<AbilityAction> actions) {
        return this.sequences.entrySet().stream()
                .filter(entry -> {
                    List<AbilityAction> tail = entry.getValue();
                    if (!actions.isEmpty() || !tail.isEmpty()) {
                        int listSize = actions.size();
                        int tailSize = tail.size();
                        if (listSize >= tailSize) {
                            for (int i = 0; i < tailSize; ++i) {
                                AbilityAction first = actions.get(listSize - 1 - i);
                                AbilityAction second = tail.get(tailSize - 1 - i);
                                if (!(first.ability() == second.ability()
                                        && second.action().equals(first.action()))) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                    return false;
                })
                .map(Map.Entry::getKey)
                .findFirst().map(declaration -> declaration);
    }


    public AbilityAction createAction(Class<? extends Ability> ability, ActivationMethod activation) {
        return new AbilityAction() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return AbilityAction.class;
            }

            @Override
            public Class<? extends Ability> ability() {
                return ability;
            }

            @Override
            public ActivationMethod action() {
                return activation;
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.action(), this.ability());
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof AbilityAction other)) return false;
                if (this == other) return true;
                return this.action().equals(other.action()) && this.ability().equals(other.ability());
            }
        };
    }

    public int getMaxActionsSize() {
        return maxActionsSize.get();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void on(AbilitySlotsReloadEvent event) {
        this.sequences.clear();
    }
}
