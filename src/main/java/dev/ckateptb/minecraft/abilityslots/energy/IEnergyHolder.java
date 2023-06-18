package dev.ckateptb.minecraft.abilityslots.energy;

public interface IEnergyHolder {
    double getEnergy();

    boolean removeEnergy(double value);

    void addEnergy(double value);

    void setEnergy(double value);

    double getMaxEnergy();

    void updateEnergyBar();

    void enableEnergyBar();

    void disableEnergyBar();

    boolean isEnergyBarEnabled();
}
