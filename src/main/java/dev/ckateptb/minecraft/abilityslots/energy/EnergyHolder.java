package dev.ckateptb.minecraft.abilityslots.energy;

public interface EnergyHolder {
    double getEnergy();

    boolean removeEnergy(double value);

    void addEnergy(double value);

    void setEnergy(double value);

    double getMaxEnergy();

    boolean isEnergyEnabled();
}
