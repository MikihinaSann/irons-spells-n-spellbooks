package be.florens.expandability.mixin.forge.swimming;

import be.florens.expandability.EventDispatcher;
import be.florens.expandability.api.EventResult;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @WrapWithCondition(
      method = {"updateFluidHeightAndDoFluidPushing(Lnet/minecraft/tags/TagKey;D)Z"},
      require = 1,
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
      )}
   )
   private boolean cancelFluidPushing(Entity entity, Vec3 vec3) {
      return !(entity instanceof Player player && EventDispatcher.onPlayerSwim(player) == EventResult.FAIL);
   }
}
