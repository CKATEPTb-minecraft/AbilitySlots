package dev.ckateptb.minecraft.abilityslots.ability.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum ActivationMethod {
    PASSIVE,
    SEQUENCE,

    LEFT_CLICK_BLOCK,
    LEFT_CLICK_ENTITY,
    LEFT_CLICK(LEFT_CLICK_BLOCK, LEFT_CLICK_ENTITY),

    RIGHT_CLICK_ENTITY,
    RIGHT_CLICK_BLOCK,
    RIGHT_CLICK(RIGHT_CLICK_ENTITY, RIGHT_CLICK_BLOCK),

    SNEAK,
    SNEAK_RELEASE,

    HAND_SWAP, // default F keybinding

    FALL;

    private final Set<ActivationMethod> equals = new HashSet<>();
    ActivationMethod(ActivationMethod... actions) {
        this.equals.addAll(Arrays.asList(actions));
        this.equals.add(this);
    }

    public boolean equals(ActivationMethod action) {
        return this.equals.contains(action);
    }
}
