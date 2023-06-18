package dev.ckateptb.minecraft.abilityslots.energy;

import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import lombok.Getter;

public class EnergyHolder implements IEnergyHolder {
    @Getter
    private double energy;
    private boolean enabled;

    @Override
    public boolean removeEnergy(double value) {
        if (this.energy >= value) {
            this.energy = this.energy - value;
            return true;
        }
        return false;
    }

    @Override
    public void addEnergy(double value) {
        this.energy = Math.min(this.getMaxEnergy(), this.energy + value);
    }

    @Override
    public void setEnergy(double value) {
        this.energy = FastMath.max(FastMath.min(value, this.getMaxEnergy()), 0);
    }

    @Override
    public double getMaxEnergy() {
        return AbilitySlotsConfig.getInstance().getEnergy().getMax();
    }

    @Override
    public void updateEnergyBar() {
    }

    @Override
    public void enableEnergyBar() {
        this.enabled = true;
    }

    @Override
    public void disableEnergyBar() {
        this.enabled = false;
    }

    @Override
    public boolean isEnergyBarEnabled() {
        return this.enabled;
    }
}
