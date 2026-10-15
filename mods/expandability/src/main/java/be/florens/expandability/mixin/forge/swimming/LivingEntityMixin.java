package be.florens.expandability.mixin.forge.swimming;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.Util;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {
   @ModifyExpressionValue(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"},
      require = 1,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"
      )}
   )
   private boolean setInWater(boolean original) {
      return (Object)this instanceof Player player ? Util.processEventResult(EventDispatcher.onPlayerSwim(player), original) : original;
   }

   @ModifyExpressionValue(
      method = {"travel(Lnet/minecraft/world/phys/Vec3;)V"},
      require = 1,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;isInLava()Z"
      )}
   )
   private boolean setInLava(boolean original) {
      return (Object)this instanceof Player player ? Util.processEventResult(EventDispatcher.onPlayerSwim(player), original) : original;
   }
}
