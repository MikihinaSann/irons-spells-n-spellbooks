package net.neoforged.neoforge.internal.fabric.mixin.api;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.neoforged.neoforge.common.extensions.IHolderSetExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge's {@code HolderSet.Named} additions — invalidation listeners that
 * fire whenever the tag rebinds, feeding composite holderset caches.
 */
@Mixin(HolderSet.Named.class)
public abstract class HolderSetNamedApiMixin<T> implements IHolderSetExtension<T> {

    @Unique
    private final List<Runnable> neoforge$invalidationCallbacks = new ArrayList<>();

    @Override
    public void addInvalidationListener(Runnable runnable) {
        neoforge$invalidationCallbacks.add(runnable);
    }

    @Inject(method = "bind", at = @At("TAIL"))
    private void neoforge$invalidateOnBind(List<Holder<T>> contents, CallbackInfo ci) {
        for (Runnable runnable : neoforge$invalidationCallbacks) {
            runnable.run();
        }
    }
}
