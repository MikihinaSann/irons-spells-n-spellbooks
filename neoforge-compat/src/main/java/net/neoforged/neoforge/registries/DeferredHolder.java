package net.neoforged.neoforge.registries;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Fabric-compatible replacement for NeoForge's {@code DeferredHolder}.
 * Implements {@link Holder} so it can be used wherever a Holder is expected.
 */
public class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    protected final ResourceKey<R> key;
    private final Supplier<T> supplier;
    private T value;
    private Holder<R> holder;
    private DeferredRegister<R> owner;

    @SuppressWarnings("unchecked")
    public DeferredHolder(ResourceKey<?> registryKey, ResourceLocation location, Supplier<T> supplier) {
        this.key = registryKey != null ? ResourceKey.create((ResourceKey<Registry<R>>) registryKey, location) : null;
        this.supplier = supplier;
    }

    /** Back-link so an unbound holder can force its owning register to drain early. */
    void setOwner(DeferredRegister<R> owner) {
        this.owner = owner;
    }

    private void resolve() {
        if (value == null && owner != null) {
            owner.accept();
        }
    }

    @SuppressWarnings("unchecked")
    void bind(Registry<R> registry) {
        T supplied = supplier.get();
        this.value = supplied;
        Registry.register(registry, key, supplied);
        this.holder = registry.getHolder(key).orElse(null);
        // Side-channel bindings for shimmed NeoForge types.
        if (supplied instanceof net.neoforged.neoforge.attachment.AttachmentType<?> attachmentType) {
            attachmentType.bindFabric(key.location());
        }
        if (supplied instanceof net.neoforged.neoforge.fluids.FluidType fluidType) {
            net.neoforged.neoforge.fluids.NeoForgeFluidTypeRegistry.registerType(key.location(), fluidType);
        }
    }

    void bindValue(Registry<R> registry) {
        bind(registry);
    }

    @SuppressWarnings("unchecked")
    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<?> registryKey, ResourceLocation id) {
        return new DeferredHolder<>(registryKey, id, () -> {
            throw new IllegalStateException("Cannot create a DeferredHolder without a supplier");
        });
    }

    public T get() {
        resolve();
        if (value == null) {
            org.slf4j.LoggerFactory.getLogger("neoforge_compat").warn("DeferredHolder accessed before registration: {}", key != null ? key.location() : "unknown");
            return null;
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    /** Alias matching {@code ResourceKey#location()} semantics. */
    public ResourceLocation location() {
        return getId();
    }

    public ResourceLocation getId() {
        return key.location();
    }

    public Optional<Holder<R>> getHolder() {
        resolve();
        return Optional.ofNullable(holder);
    }

    public Holder<R> asHolder() {
        resolve();
        return holder;
    }

    public DeferredHolder<R, T> toDeferredHolder() {
        return this;
    }

    public boolean is(ResourceLocation location) {
        return this.key.location().equals(location);
    }

    // Holder<R> interface methods
    @Override
    public R value() {
        return get();
    }

    @Override
    public boolean isBound() {
        return value != null;
    }

    @Override
    public boolean is(ResourceKey<R> key) {
        return this.key == key;
    }

    @Override
    public boolean is(Holder<R> holder) {
        return this.holder != null && this.holder.is(holder);
    }

    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return Optional.ofNullable(key);
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.left(key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> owner) {
        resolve();
        return holder != null ? holder.canSerializeIn(owner) : true;
    }

    @Override
    public Stream<TagKey<R>> tags() {
        resolve();
        return holder != null ? holder.tags() : Stream.empty();
    }

    @Override
    public boolean is(TagKey<R> tag) {
        resolve();
        return holder != null && holder.is(tag);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(key);
    }

    @Override
    public String toString() {
        return "DeferredHolder[" + key.location() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DeferredHolder<?, ?> that) {
            return Objects.equals(key, that.key);
        }
        resolve();
        if (holder != null) {
            return holder.equals(o);
        }
        if (o instanceof Holder<?> other) {
            Optional<?> otherKey = other.unwrapKey();
            return otherKey.isPresent() && Objects.equals(key, otherKey.get());
        }
        return false;
    }

    @Override
    public int hashCode() {
        resolve();
        if (holder != null) {
            return System.identityHashCode(holder);
        }
        return key.hashCode();
    }
}
