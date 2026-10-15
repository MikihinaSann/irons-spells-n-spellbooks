package be.florens.expandability.mixin.swimming;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.Util;
import be.florens.expandability.api.EventResult;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
   public Level level;

   @Shadow
   public abstract boolean isInWater();

   @ModifyExpressionValue(
      method = {"updateSwimming()V", "isVisuallyCrawling()Z", "canSpawnSprintParticle()Z", "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},
      require = 4,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;isInWater()Z"
      )}
   )
   private boolean setInWater(boolean original) {
      return (Object)this instanceof Player player ? Util.processEventResult(EventDispatcher.onPlayerSwim(player), original) : original;
   }

   @ModifyExpressionValue(
      method = {"updateSwimming()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;isUnderWater()Z"
      )}
   )
   private boolean setUnderWater(boolean original) {
      return (Object)this instanceof Player player ? Util.processEventResult(EventDispatcher.onPlayerSwim(player), original) : original;
   }

   @WrapWithCondition(
      method = {"move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;waterSwimSound()V"
      )}
   )
   private boolean cancelPlaySwimSound(Entity entity) {
      return !(!this.isInWater() && entity instanceof Player player) || EventDispatcher.onPlayerSwim(player) != EventResult.SUCCESS;
   }

   @WrapWithCondition(
      method = {"updateInWaterStateAndDoWaterCurrentPushing()V", "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;resetFallDistance()V"
      )}
   )
   private boolean cancelResetFallDistance(Entity entity) {
      return !(entity instanceof Player player && EventDispatcher.onPlayerSwim(player) == EventResult.FAIL);
   }

   @ModifyExpressionValue(
      method = {"getBlockSpeedFactor()F"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
      )}
   )
   private boolean fixBlockSpeedFactor(boolean original) {
      if ((Object)this instanceof Player player) {
         BlockState block = player.level().getBlockState(player.blockPosition());
         if (block.is(Blocks.WATER) && EventDispatcher.onPlayerSwim(player) == EventResult.FAIL) {
            return true;
         }
      }

      return original;
   }
}
