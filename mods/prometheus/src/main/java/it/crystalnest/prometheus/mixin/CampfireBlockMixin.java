package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeChanger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.CampfireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({CampfireBlock.class})
public abstract class CampfireBlockMixin implements FireTypeChanger {
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

   @WrapOperation(
      method = {"entityInside"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )}
   )
   private boolean redirectHurt(Entity instance, DamageSource damageSource, float damage, Operation<Boolean> original) {
      return FireManager.affect(
         instance, this.getFireType(), Fire::getOnCampfire, (xva$0, xva$1, xva$2) -> (Boolean)original.call(new Object[]{xva$0, xva$1, xva$2})
      );
   }
}
