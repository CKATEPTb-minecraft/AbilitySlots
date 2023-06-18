package dev.ckateptb.minecraft.abilityslots.energy.config;

import lombok.Getter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class EnergyConfig {
    @Comment(value = "Use ability energy to prevent spam", override = true)
    private boolean enabled = true;
    private String name = "§5Ability Energy";
    @Comment(value = "Available types: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE", override = true)
    private String energyColor = BarColor.YELLOW.name();
    @Comment(value = "Available types: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20", override = true)
    private String energyStyle = BarStyle.SOLID.name();
    @Comment(value = "Base energy limit")
    private double max = 100;
    @Comment(value = "The amount of resource that will be restored every second")
    private double regen = 5;

    public BarColor getEnergyColor() {
        return BarColor.valueOf(this.energyColor);
    }

    public BarStyle getEnergyStyle() {
        return BarStyle.valueOf(this.energyStyle);
    }
}
