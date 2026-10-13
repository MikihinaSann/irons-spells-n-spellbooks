package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.client.ISupportTransmog;
import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorStand.class})
public class ClientArmorStandMixin implements ISupportTransmog {
   @Shadow
   @Final
   private NonNullList<ItemStack> armorItems;

   @Inject(
      method = {"getItemBySlot"},
      at = {@At("RETURN")},
      cancellable = true
   )
   void irons_lib$replaceTransmogStack(EquipmentSlot slot1, CallbackInfoReturnable<ItemStack> cir) {
      if (slot1.isArmor()) {
         ItemStack stack = (ItemStack)cir.getReturnValue();
         if (TransmogClientHandler.isTransmogRenderActive() && TransmogItemData.has(stack)) {
            cir.setReturnValue(ItemStack.EMPTY);
         }
      }
   }

   @Override
   public ItemStack irons_lib$getArmorBySlotRaw(EquipmentSlot equipmentSlot) {
      return !equipmentSlot.isArmor() ? ItemStack.EMPTY : (ItemStack)(Object) this.armorItems.get(equipmentSlot.getIndex());
   }
}
