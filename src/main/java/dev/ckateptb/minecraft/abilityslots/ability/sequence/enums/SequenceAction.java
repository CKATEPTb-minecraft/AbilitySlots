package dev.ckateptb.minecraft.abilityslots.ability.sequence.enums;


public enum SequenceAction {
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

    private final SequenceAction[] equals;

    SequenceAction(SequenceAction... actions) {
        this.equals = actions;
    }

    public boolean equals(SequenceAction action) {
        for (SequenceAction equal : equals) {
            if (action == equal) return true;
        }
        return action == this;
    }
}
