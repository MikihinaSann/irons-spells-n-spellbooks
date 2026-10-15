package it.crystalnest.prometheus.mixin;

import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.FireChargeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FireChargeItem.class})
public abstract class FireChargeItemMixin {
   @Inject(
      method = {"asProjectile"},
      at = {@At("RETURN")}
   )
   private void onAsProjectile(CallbackInfoReturnable<Projectile> cir) {
      ((FireTypeChanger)(Object)cir.getReturnValue()).setFireType(FireManager.getFireType(Fire.Component.FIRE_CHARGE_ITEM, (FireChargeItem)(Object)this));
   }
}
