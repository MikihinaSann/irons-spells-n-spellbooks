package net.neoforged.neoforge.energy;

/** NeoForge energy capability interface (RF-style). */
public interface IEnergyStorage {
    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    int getEnergyStored();

    int getMaxEnergyStored();

    default boolean canExtract() {
        return true;
    }

    default boolean canReceive() {
        return true;
    }
}
