package net.neoforged.neoforge.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
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

    /**
     * NeoForge's registerBlock — a block-level provider receiving
     * (level, pos, state, blockEntity-or-null, side).
     */
    public <T> void registerBlock(Capability<T> capability, IBlockCapabilityProvider<T, Direction> provider, Block... blocks) {
        blockRegistrations.add(new BlockRegistration<>(capability, provider, blocks));
    }

    public List<BlockRegistration<?>> getBlockRegistrations() {
        return blockRegistrations;
    }

    private final List<BlockRegistration<?>> blockRegistrations = new ArrayList<>();

    public record BlockRegistration<T>(Capability<T> capability, IBlockCapabilityProvider<T, Direction> provider, Block[] blocks) {
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
