package dev.ckateptb.minecraft.abilityslots.protection;

import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;

public abstract class AbstractProtection implements Protection {
    protected final AbilitySlotsConfig config;

    protected AbstractProtection(AbilitySlotsConfig config) {
        this.config = config;
    }
}