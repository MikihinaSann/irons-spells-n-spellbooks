package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractHurtingProjectile.class})
public abstract class AbstractHurtingProjectileMixin implements FireTyped {
   @WrapOperation(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/projectile/AbstractHurtingProjectile;igniteForSeconds(F)V"
      )}
   )
   private void wrapIgniteForSeconds(AbstractHurtingProjectile instance, float seconds, Operation<Void> original) {
      FireManager.setOnFire(instance, seconds, this.getFireType(), (xva$0, xva$1) -> {
         Void var10000 = (Void)original.call(new Object[]{xva$0, xva$1});
      });
   }
}
