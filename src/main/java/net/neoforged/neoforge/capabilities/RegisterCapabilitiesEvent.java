package net.neoforged.neoforge.capabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects capability registrations; drained at bootstrap.
 */
public class RegisterCapabilitiesEvent extends Event {
    private final List<Registration<?, ?>> registrations = new ArrayList<>();

    public <T, B extends BlockEntity> void registerBlockEntity(Capability<T> capability, BlockEntityType<B> type, CapabilityProvider<T, B> provider) {
        registrations.add(new Registration<>(capability, type, provider));
    }

    public <T, B extends BlockEntity> void registerBlockEntity(Capability<T> capability, CapabilityProvider<T, B> provider, BlockEntityType<B>... types) {
        for (BlockEntityType<B> type : types) {
            registrations.add(new Registration<>(capability, type, provider));
        }
    }

    public List<Registration<?, ?>> getRegistrations() {
        return registrations;
    }

    public record Registration<T, B extends BlockEntity>(Capability<T> capability, BlockEntityType<B> type, CapabilityProvider<T, B> provider) {
    }

    @FunctionalInterface
    public interface CapabilityProvider<T, B extends BlockEntity> {
        T get(B blockEntity, net.minecraft.core.Direction direction);
    }

    /** Placeholder capability descriptor. */
    public record Capability<T>(net.minecraft.resources.ResourceLocation id, Class<T> type) {
    }
}
