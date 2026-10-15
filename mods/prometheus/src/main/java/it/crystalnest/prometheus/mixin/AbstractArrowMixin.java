package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AbstractArrow.class})
public abstract class AbstractArrowMixin implements FireTypeChanger {
   @WrapOperation(
      method = {"onHitEntity"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
      )}
   )
   private void redirectSetSecondsOnFire(Entity instance, float seconds, Operation<Void> original) {
      FireManager.setOnFire(instance, seconds, this.getFireType(), (xva$0, xva$1) -> {
         Void var10000 = (Void)original.call(new Object[]{xva$0, xva$1});
      });
   }
}
