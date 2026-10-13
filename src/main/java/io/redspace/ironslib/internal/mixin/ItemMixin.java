package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.kinetic_weapon.KineticWeapon;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Item.class})
public class ItemMixin {
   @Inject(
      method = {"getUseDuration"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void irons_lib$kineticUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
      if (cir.getReturnValueI() == 0 && KineticWeapon.has(stack)) {
         cir.setReturnValue(72000);
      }
   }

   @Inject(
      method = {"use"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void irons_lib$kineticUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
      if (((InteractionResultHolder)cir.getReturnValue()).getResult() == InteractionResult.PASS) {
         ItemStack stack = player.getItemInHand(hand);
         KineticWeapon kineticWeapon = KineticWeapon.get(stack);
         if (kineticWeapon != null) {
            player.startUsingItem(hand);
            if (!level.isClientSide()) {
               kineticWeapon.sound()
                  .ifPresent(sound -> level.playSound(null, player.getX(), player.getY(), player.getZ(), sound, player.getSoundSource(), 1.0F, 1.0F));
            }

            cir.setReturnValue(InteractionResultHolder.consume(stack));
         }
      }
   }
}
