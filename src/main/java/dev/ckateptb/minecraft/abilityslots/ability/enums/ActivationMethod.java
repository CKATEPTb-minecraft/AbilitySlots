package dev.ckateptb.minecraft.abilityslots.ability.enums;

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

    HAND_SWAP, // default F keybind

    FALL;

    private final ActivationMethod[] equals;

    ActivationMethod(ActivationMethod... actions) {
        this.equals = actions;
    }

    public boolean equals(ActivationMethod action) {
        for (ActivationMethod equal : equals) {
            if (action == equal) return true;
        }
        return action == this;
    }
}
