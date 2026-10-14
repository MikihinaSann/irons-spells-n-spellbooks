package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge parity: AbstractFurnaceBlockEntity consults ForgeHooks.getBurnTime
 * (which fires FurnaceFuelBurnTimeEvent) instead of the vanilla fuel map.
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceFuelBurnTimeDispatchMixin {

    @Inject(method = "getBurnDuration", at = @At("HEAD"), cancellable = true)
    private void neoforge$fireBurnTimeEvent(ItemStack fuel, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(CommonHooks.getBurnTime(fuel, null));
    }

    @Inject(method = "isFuel", at = @At("HEAD"), cancellable = true)
    private static void neoforge$isFuelEvent(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(CommonHooks.getBurnTime(stack, null) > 0);
    }
}
