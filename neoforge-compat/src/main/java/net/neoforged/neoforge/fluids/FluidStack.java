package net.neoforged.neoforge.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * NeoForge-style FluidStack (fluid + amount + data components), implemented as a
 * {@link DataComponentHolder} over vanilla {@link Fluid}.
 */
public class FluidStack implements DataComponentHolder {
    public static final FluidStack EMPTY = new FluidStack();
    public static final int BUCKET_VOLUME = 1000;

    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.holderByNameCodec().fieldOf("id").forGetter(s -> s.fluidHolder),
            Codec.INT.optionalFieldOf("amount", 0).forGetter(s -> s.amount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(s -> s.components.asPatch())
    ).apply(instance, FluidStack::new));

    public static final Codec<FluidStack> OPTIONAL_CODEC = CODEC;
    public static final Codec<FluidStack> NON_EMPTY_CODEC = CODEC;

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC = StreamCodec.of(
            (buf, stack) -> {
                buf.writeBoolean(!stack.isEmpty());
                if (!stack.isEmpty()) {
                    buf.writeVarInt(BuiltInRegistries.FLUID.getId(stack.getFluid()));
                    buf.writeVarInt(stack.getAmount());
                    DataComponentPatch.STREAM_CODEC.encode(buf, stack.components.asPatch());
                }
            },
            buf -> {
                if (!buf.readBoolean()) {
                    return EMPTY;
                }
                var fluid = BuiltInRegistries.FLUID.getHolder(buf.readVarInt()).orElseThrow();
                int amount = buf.readVarInt();
                var patch = DataComponentPatch.STREAM_CODEC.decode(buf);
                return new FluidStack(fluid, amount, patch);
            });

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> OPTIONAL_STREAM_CODEC = STREAM_CODEC;

    private Holder<Fluid> fluidHolder;
    @Nullable
    private java.util.function.Supplier<Holder<Fluid>> lazyHolder;
    private int amount;
    private PatchedDataComponentMap components;

    private FluidStack() {
        this.fluidHolder = net.minecraft.world.level.material.Fluids.EMPTY.builtInRegistryHolder();
        this.amount = 0;
        this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
    }

    public FluidStack(Holder<Fluid> fluid, int amount) {
        this(fluid, amount, DataComponentPatch.EMPTY);
    }

    @SuppressWarnings("unchecked")
    public FluidStack(DeferredHolder<Fluid, ? extends Fluid> fluid, int amount) {
        this.fluidHolder = net.minecraft.world.level.material.Fluids.EMPTY.builtInRegistryHolder();
        this.amount = amount;
        this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
        this.lazyHolder = () -> (Holder<Fluid>) (Holder<?>) fluid.asHolder();
    }

    public FluidStack(Fluid fluid, int amount) {
        this(fluid.builtInRegistryHolder(), amount);
    }

    public FluidStack(Holder<Fluid> fluid, int amount, DataComponentPatch patch) {
        this.fluidHolder = fluid;
        this.amount = amount;
        this.components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
        this.components.applyPatch(patch);
    }

    public static FluidStack parse(HolderLookup.Provider provider, Tag tag) {
        var result = CODEC.parse(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), tag);
        return result.result().orElseThrow(() -> new IllegalArgumentException("Invalid fluid stack tag: " + tag));
    }

    public static FluidStack parseOptional(HolderLookup.Provider provider, CompoundTag tag) {
        if (tag.isEmpty()) {
            return EMPTY;
        }
        var result = CODEC.parse(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), tag);
        return result.result().orElse(EMPTY);
    }

    public static FluidStack parseOptional(HolderLookup.Provider provider, net.minecraft.nbt.Tag tag) {
        if (tag == null || (tag instanceof CompoundTag c && c.isEmpty())) {
            return EMPTY;
        }
        var result = CODEC.parse(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), tag);
        return result.result().orElse(EMPTY);
    }

    public CompoundTag save(HolderLookup.Provider provider) {
        return (CompoundTag) CODEC.encodeStart(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), this)
                .getOrThrow();
    }

    public CompoundTag save(HolderLookup.Provider provider, CompoundTag output) {
        output.merge(save(provider));
        return output;
    }

    public boolean isEmpty() {
        return this == EMPTY || amount <= 0 || getFluid() == net.minecraft.world.level.material.Fluids.EMPTY;
    }

    public Fluid getFluid() {
        return getFluidHolder().value();
    }

    public Holder<Fluid> getFluidHolder() {
        if (lazyHolder != null) {
            Holder<Fluid> resolved = lazyHolder.get();
            if (resolved != null) {
                fluidHolder = resolved;
                lazyHolder = null;
            }
        }
        return fluidHolder;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void grow(int amount) {
        this.amount += amount;
    }

    public void shrink(int amount) {
        this.amount -= amount;
    }

    public FluidType getFluidType() {
        Fluid fluid = getFluid();
        if (fluid instanceof FluidTypeCarrier carrier) {
            return carrier.getFluidType();
        }
        return NeoForgeFluidTypeRegistry.getTypeFor(fluid);
    }

    public FluidState getState() {
        return getFluid().defaultFluidState();
    }

    public FluidStack copy() {
        if (isEmpty()) {
            return EMPTY;
        }
        return new FluidStack(fluidHolder, amount, components.asPatch());
    }

    public FluidStack copyWithAmount(int amount) {
        FluidStack copy = copy();
        copy.setAmount(amount);
        return copy;
    }

    public FluidStack copyAndClear() {
        FluidStack copy = copy();
        this.amount = 0;
        return copy;
    }

    public boolean is(Fluid fluid) {
        return getFluid() == fluid;
    }

    public boolean is(DeferredHolder<Fluid, ? extends Fluid> fluid) {
        return getFluid() == fluid.get();
    }

    public boolean is(Holder<Fluid> fluid) {
        return fluidHolder == fluid || getFluid() == fluid.value();
    }

    public boolean is(Predicate<Holder<Fluid>> predicate) {
        return predicate.test(fluidHolder);
    }

    public boolean is(TagKey<Fluid> tag) {
        return fluidHolder.is(tag);
    }

    public boolean is(ResourceKey<Fluid> key) {
        return fluidHolder.is(key);
    }

    public net.minecraft.network.chat.Component getHoverName() {
        return getFluidType().getDescription(this);
    }

    public static boolean isSameFluid(FluidStack a, FluidStack b) {
        return a.is(b.getFluid());
    }

    public static boolean isSameFluidSameComponents(FluidStack a, FluidStack b) {
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        }
        return a.is(b.getFluid()) && a.components.asPatch().equals(b.components.asPatch());
    }

    public static boolean matches(FluidStack a, FluidStack b) {
        return isSameFluidSameComponents(a, b);
    }

    @Override
    public DataComponentMap getComponents() {
        return components;
    }

    @Nullable
    /**
     * Supplier-form component accessors (accept {@code DeferredHolder}s).
     */
    public <T> T get(java.util.function.Supplier<? extends DataComponentType<? extends T>> type) {
        return get(type.get());
    }

    public <T> T getOrDefault(java.util.function.Supplier<? extends DataComponentType<? extends T>> type, T fallback) {
        return getOrDefault(type.get(), fallback);
    }

    public <T> T set(java.util.function.Supplier<? extends DataComponentType<? super T>> type, @Nullable T value) {
        return set(type.get(), value);
    }

    public <T> void remove(java.util.function.Supplier<? extends DataComponentType<? extends T>> type) {
        remove(type.get());
    }

    public <T> boolean has(java.util.function.Supplier<? extends DataComponentType<? extends T>> type) {
        return has(type.get());
    }

    public <T> T set(DataComponentType<? super T> type, @Nullable T value) {
        return components.set(type, value);
    }

    @Nullable
    public <T> T remove(DataComponentType<? extends T> type) {
        return components.remove(type);
    }

    public void applyComponents(DataComponentPatch patch) {
        components.applyPatch(patch);
    }

    public void applyComponents(DataComponentMap map) {
        components.setAll(map);
    }

    public DataComponentPatch getComponentsPatch() {
        return components.asPatch();
    }

    public boolean componentsMatch(FluidStack other) {
        return components.asPatch().equals(other.components.asPatch());
    }
}
