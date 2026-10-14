package net.neoforged.neoforge.internal.fabric.mixin.api;

import net.minecraft.core.HolderSet;
import net.neoforged.neoforge.common.extensions.IHolderSetExtension;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Marker mixin — {@code HolderSet.Direct} gains {@link IHolderSetExtension}'s
 * default serialization-type/invalidation behavior (direct sets never
 * rebind, so the no-op listener is correct).
 */
@Mixin(HolderSet.Direct.class)
public abstract class HolderSetDirectApiMixin<T> implements IHolderSetExtension<T> {
}
