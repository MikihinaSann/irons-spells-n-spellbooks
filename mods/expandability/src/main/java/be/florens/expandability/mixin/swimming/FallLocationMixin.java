package be.florens.expandability.mixin.swimming;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.Util;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.damagesource.FallLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({FallLocation.class})
public abstract class FallLocationMixin {
   @ModifyExpressionValue(
      method = {"getCurrentFallLocation(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/damagesource/FallLocation;"},
      require = 0,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"
      )}
   )
   private static boolean setInWater(boolean original, LivingEntity entity) {
      return entity instanceof Player player ? Util.processEventResult(EventDispatcher.onPlayerSwim(player), original) : original;
   }
}
