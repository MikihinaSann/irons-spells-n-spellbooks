package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's {@code CommonHooks.shouldSuppressEnderManAnger} replaces the
 * carved-pumpkin check with {@code Item.isEnderMask}, letting armor like
 * PumpkinArmorItem mask the player too.
 */
@Mixin(EnderMan.class)
public abstract class EnderManMaskMixin {

    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$enderMaskCheck(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack headStack = player.getInventory().armor.get(3);
        if (headStack.getItem().isEnderMask(headStack, player, (EnderMan) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}
