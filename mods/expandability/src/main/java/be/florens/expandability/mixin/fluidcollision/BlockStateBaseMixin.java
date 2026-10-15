package be.florens.expandability.mixin.fluidcollision;

import be.florens.expandability.EventDispatcher;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BlockStateBase.class})
public abstract class BlockStateBaseMixin {
   @Shadow
   public abstract FluidState getFluidState();

   @ModifyReturnValue(
      method = {"getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"},
      at = {@At("RETURN")}
   )
   private VoxelShape addFluidCollision(VoxelShape originalShape, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      FluidState fluidState = this.getFluidState();
      if (collisionContext instanceof EntityCollisionContext entityCollisionContext
         && entityCollisionContext.getEntity() instanceof LivingEntity entity
         && !fluidState.isEmpty()
         && blockGetter.getFluidState(blockPos.above()).isEmpty()) {
         double stepHeight = entity.getAttributeValue(Attributes.STEP_HEIGHT);
         VoxelShape fluidShape = Shapes.box(0.0, 0.0, 0.0, 1.0, fluidState.getHeight(blockGetter, blockPos), 1.0);
         return collisionContext.isAbove(fluidShape.move(0.0, -stepHeight, 0.0), blockPos, false) && EventDispatcher.onLivingFluidCollision(entity, fluidState)
            ? Shapes.or(fluidShape, originalShape)
            : originalShape;
      } else {
         return originalShape;
      }
   }
}
