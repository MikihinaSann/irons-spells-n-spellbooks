package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.client.ISupportTransmog;
import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public class ClientPlayerMixin implements ISupportTransmog {
   @Shadow
   @Final
   Inventory inventory;

   @Inject(
      method = {"isModelPartShown"},
      at = {@At("RETURN")},
      cancellable = true
   )
   void irons_lib$hideJacketLayers(PlayerModelPart part, CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue()) {
         Player self = (Player)(Object) this;

         boolean disable = switch (part) {
            case HAT -> TransmogClientHandler.shouldDisableOuterLayer(self, EquipmentSlot.HEAD);
            case JACKET, LEFT_SLEEVE, RIGHT_SLEEVE -> TransmogClientHandler.shouldDisableOuterLayer(self, EquipmentSlot.CHEST);
            case LEFT_PANTS_LEG, RIGHT_PANTS_LEG -> {
               if (!TransmogClientHandler.shouldDisableOuterLayer(self, EquipmentSlot.LEGS)
                  && !TransmogClientHandler.shouldDisableOuterLayer(self, EquipmentSlot.FEET)) {
                  yield false;
               }

               yield true;
            }
            default -> false;
         };
         if (disable) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(
      method = {"getItemBySlot"},
      at = {@At("RETURN")},
      cancellable = true
   )
   void irons_lib$replaceTransmogStack(EquipmentSlot slot1, CallbackInfoReturnable<ItemStack> cir) {
      if (slot1.isArmor()) {
         if (TransmogClientHandler.hideForTransmog((Player)(Object) this, (ItemStack)cir.getReturnValue())) {
            cir.setReturnValue(ItemStack.EMPTY);
         }
      }
   }

   @Override
   public ItemStack irons_lib$getArmorBySlotRaw(EquipmentSlot equipmentSlot) {
      return !equipmentSlot.isArmor() ? ItemStack.EMPTY : this.inventory.getArmor(equipmentSlot.getIndex());
   }
}
