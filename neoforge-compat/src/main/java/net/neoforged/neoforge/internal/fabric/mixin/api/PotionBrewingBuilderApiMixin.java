package net.neoforged.neoforge.internal.fabric.mixin.api;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.neoforge.common.extensions.IBrewingBuilderExtension;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Applies {@link IBrewingBuilderExtension} to {@code PotionBrewing.Builder} at
 * runtime — injected_interfaces only covers compile-time in dev.
 */
@Mixin(PotionBrewing.Builder.class)
public abstract class PotionBrewingBuilderApiMixin implements IBrewingBuilderExtension {
}
