package net.neoforged.neoforge.internal.fabric.mixin;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla {@code Registry.safeCastToReference} requires {@code Holder.Reference}
 * when a {@code holderByNameCodec}/{@code byNameCodec} encodes a holder —
 * NeoForge solves the same problem by patching {@code Holder.getDelegate()}
 * which {@code DeferredHolder} overrides to unwrap its bound reference. We do
 * the equivalent here: a bound {@link DeferredHolder} resolves to its real
 * registry {@code Holder.Reference} instead of failing with
 * "Unregistered holder" (crash seen saving mob effects on entity NBT write).
 */
@Mixin(Registry.class)
public interface RegistrySafeCastMixin {

    @SuppressWarnings("unchecked")
    @Inject(method = "safeCastToReference", at = @At("HEAD"), cancellable = true)
    private <T> void neoforgecompat$safeCastDeferred(Holder<T> holder,
            CallbackInfoReturnable<DataResult<Holder.Reference<T>>> cir) {
        if (holder instanceof DeferredHolder<?, ?> deferred) {
            Holder<T> bound = (Holder<T>) deferred.asHolder();
            if (bound instanceof Holder.Reference<T> reference) {
                cir.setReturnValue(DataResult.success(reference));
            }
        }
    }
}
