package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({SmallFireball.class})
public abstract class SmallFireballMixin extends Entity implements FireTyped {
   public SmallFireballMixin(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   @WrapOperation(
      method = {"onHitEntity"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
      )}
   )
   private void wrapIgniteForSeconds(Entity instance, float seconds, Operation<Void> original) {
      FireManager.setOnFire(instance, seconds, this.getFireType(), (xva$0, xva$1) -> {
         Void var10000 = (Void)original.call(new Object[]{xva$0, xva$1});
      });
   }

   public void clearFire() {
   }
}
