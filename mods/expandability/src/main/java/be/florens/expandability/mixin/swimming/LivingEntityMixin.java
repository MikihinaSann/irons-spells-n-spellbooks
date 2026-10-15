package be.florens.expandability.mixin.swimming;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.Util;
import be.florens.expandability.api.EventResult;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
   public LivingEntityMixin(EntityType<?> entityType, Level level) {
      super(entityType, level);
   }

   @ModifyExpressionValue(
      method = {"aiStep()V"},
      require = 2,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tags/TagKey;)D"
      )}
   )
   private double setFluidHeight(double original) {
      if ((Object)this instanceof Player player) {
         EventResult shouldSwim = EventDispatcher.onPlayerSwim(player);
         return Util.processEventResult(shouldSwim, 1.0, 0.0, original);
      } else {
         return original;
      }
   }

   @ModifyExpressionValue(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V", "aiStep()V", "checkFallDamage(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V"},
      require = 3,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"
      )}
   )
   private boolean setInWater(boolean original) {
      return (Object)this instanceof Player player ? Util.processEventResult(EventDispatcher.onPlayerSwim(player), original) : original;
   }

   @Inject(
      method = {"checkFallDamage(DZLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V"},
      at = {@At("HEAD")}
   )
   private void resetFallHeight(CallbackInfo info) {
      if ((Object)this instanceof Player player && EventDispatcher.onPlayerSwim(player) == EventResult.SUCCESS) {
         this.fallDistance = 0.0F;
      }
   }

   @ModifyExpressionValue(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"},
      allow = 2,
      require = 2,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;isFree(DDD)Z"
      )}
   )
   private boolean cancelLeaveFluidAssist(boolean original) {
      return (Object)this instanceof Player player && EventDispatcher.onPlayerSwim(player) == EventResult.SUCCESS ? false : original;
   }
}
