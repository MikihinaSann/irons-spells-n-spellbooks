package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Consults {@link BrewingRecipeRegistry} where NeoForge's {@code PotionBrewing}
 * patch does: ingredient admission, mix detection and the mix itself.
 */
@Mixin(PotionBrewing.class)
public abstract class PotionBrewingDispatchMixin {

    @Inject(method = "isIngredient", at = @At("RETURN"), cancellable = true)
    private void neoforgeCompat$isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && BrewingRecipeRegistry.isValidIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasPotionMix", at = @At("RETURN"), cancellable = true)
    private void neoforgeCompat$hasPotionMix(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && BrewingRecipeRegistry.hasOutput(input, ingredient)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasMix", at = @At("RETURN"), cancellable = true)
    private void neoforgeCompat$hasMix(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && BrewingRecipeRegistry.hasOutput(input, ingredient)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At("RETURN"), cancellable = true)
    private void neoforgeCompat$mix(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue().isEmpty()) {
            ItemStack custom = BrewingRecipeRegistry.getOutput(input, ingredient);
            if (!custom.isEmpty()) {
                cir.setReturnValue(custom);
            }
        }
    }
}
