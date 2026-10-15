package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.CustomFireBlock;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({BaseFireBlock.class})
public abstract class BaseFireBlockMixin implements FireTypeChanger {
   @Unique
   private ResourceLocation fireType;

   @Override
   public ResourceLocation getFireType() {
      return this.fireType;
   }

   @Override
   public void setFireType(ResourceLocation fireType) {
      this.fireType = fireType;
   }

   @ModifyReturnValue(
      method = {"getState"},
      at = {@At("RETURN")}
   )
   private static BlockState modifyGetState(BlockState original, BlockGetter level, BlockPos pos) {
      return FireManager.getComponentListList(Fire.Component.SOURCE_BLOCK)
         .stream()
         .flatMap(Collection::stream)
         .filter(source -> canSurvive(source, level.getBlockState(pos.below())))
         .findFirst()
         .<BlockState>map(Block::defaultBlockState)
         .orElse(original);
   }

   @WrapOperation(
      method = {"entityInside"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )}
   )
   private boolean redirectHurt(Entity instance, DamageSource damageSource, float damage, Operation<Boolean> original) {
      return FireManager.affect(
         instance, this.getFireType(), Fire::getInFire, (xva$0, xva$1, xva$2) -> (Boolean)original.call(new Object[]{xva$0, xva$1, xva$2})
      );
   }

   @Unique
   private static boolean canSurvive(Block source, BlockState base) {
      return source instanceof CustomFireBlock customFireBlock && customFireBlock.canSurvive(base);
   }
}
