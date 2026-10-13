package net.neoforged.neoforge.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * Entities carrying extra spawn-time data. On Fabric the platform bridge sends
 * a follow-up payload right after the spawn packet; see
 * {@code EntitySpawnDataNetworking}.
 */
public interface IEntityWithComplexSpawn {

    void writeSpawnData(RegistryFriendlyByteBuf buffer);

    void readSpawnData(RegistryFriendlyByteBuf additionalData);
}
