package dev.ckateptb.minecraft.abilityslots.protection.config;

import lombok.Getter;

@Getter
public class ProtectionConfig {
    private long cacheDuration = 60000;
    private boolean respectWorldGuard = true;
    private boolean respectGriefPrevention = true;
    private boolean respectTowny = true;
    private boolean respectLWC = true;
}
