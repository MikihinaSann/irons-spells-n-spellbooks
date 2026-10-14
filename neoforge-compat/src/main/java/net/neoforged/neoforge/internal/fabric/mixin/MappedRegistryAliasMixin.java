package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryAliases;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * NeoForge's Registry.addAlias: alias ids resolve to the entry registered under
 * their target id. Implemented by translating the looked-up id in get/getHolder/
 * containsKey (by name and by key) — the target's existing holder answers for
 * the alias, so no duplicate holder is ever created and serialization keeps the
 * canonical target id.
 */
@Mixin(MappedRegistry.class)
public abstract class MappedRegistryAliasMixin {

    @WrapOperation(method = {"get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;",
            "getHolder(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;"},
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object neoforge_compat$aliasLookup(java.util.Map<?, ?> map, Object key, Operation<Object> original) {
        if (key instanceof ResourceLocation rl) {
            ResourceLocation resolved = RegistryAliases.resolve((MappedRegistry<?>) (Object) this, rl);
            if (resolved != null) {
                return original.call(map, resolved);
            }
        }
        return original.call(map, key);
    }

    // containsKey(ResourceLocation) delegates to Map.containsKey, not Map.get —
    // it needs an explicit HEAD injection rather than the WrapOperation above.
    @Inject(method = "containsKey(Lnet/minecraft/resources/ResourceLocation;)Z",
            at = @At("HEAD"), cancellable = true)
    private void neoforge_compat$aliasContainsKeyByName(ResourceLocation id, CallbackInfoReturnable<Boolean> cir) {
        ResourceLocation resolved = RegistryAliases.resolve((MappedRegistry<?>) (Object) this, id);
        if (resolved != null) {
            cir.setReturnValue(((MappedRegistry<?>) (Object) this).containsKey(resolved));
        }
    }

    // ResourceKey overloads only apply when the key names THIS registry, and the
    // answer is whatever the real target resolves to — never unconditionally true.
    @Inject(method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;",
            at = @At("HEAD"), cancellable = true)
    private void neoforge_compat$aliasGetByKey(ResourceKey<?> key, CallbackInfoReturnable<Object> cir) {
        MappedRegistry<?> self = (MappedRegistry<?>) (Object) this;
        ResourceLocation resolved = RegistryAliases.resolve(self, key);
        if (resolved != null) {
            cir.setReturnValue(self.get(resolved));
        }
    }

    @Inject(method = "getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;",
            at = @At("HEAD"), cancellable = true)
    private void neoforge_compat$aliasGetHolderByKey(ResourceKey<?> key,
            CallbackInfoReturnable<Optional<Holder.Reference<?>>> cir) {
        MappedRegistry<?> self = (MappedRegistry<?>) (Object) this;
        ResourceLocation resolved = RegistryAliases.resolve(self, key);
        if (resolved != null) {
            cir.setReturnValue((Optional) self.getHolder(resolved));
        }
    }

    @Inject(method = "containsKey(Lnet/minecraft/resources/ResourceKey;)Z",
            at = @At("HEAD"), cancellable = true)
    private void neoforge_compat$aliasContainsKey(ResourceKey<?> key, CallbackInfoReturnable<Boolean> cir) {
        MappedRegistry<?> self = (MappedRegistry<?>) (Object) this;
        ResourceLocation resolved = RegistryAliases.resolve(self, key);
        if (resolved != null) {
            cir.setReturnValue(self.containsKey(resolved));
        }
    }
}
