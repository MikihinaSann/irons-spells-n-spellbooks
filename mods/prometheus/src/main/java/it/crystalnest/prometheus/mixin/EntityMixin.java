package it.crystalnest.prometheus.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeSynched;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class EntityMixin implements FireTypeSynched {
   @Unique
   private static final EntityDataAccessor<String> DATA_FIRE_TYPE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.STRING);
   @Unique
   private static final String FIRE_TYPE_TAG = "FireType";
   @Final
   @Shadow
   protected SynchedEntityData entityData;
   @Shadow
   private Level level;

   @Shadow
   public abstract int getRemainingFireTicks();

   @Shadow
   public abstract boolean fireImmune();

   @Override
   public ResourceLocation getFireType() {
      return ResourceLocation.tryParse((String)this.entityData.get(DATA_FIRE_TYPE));
   }

   @Override
   public void setFireType(ResourceLocation fireType) {
      if (!this.fireImmune()) {
         this.entityData.set(DATA_FIRE_TYPE, FireManager.ensure(fireType).toString());
      }
   }

   @Override
   public EntityDataAccessor<String> fireTypeAccessor() {
      return DATA_FIRE_TYPE;
   }

   @WrapOperation(
      method = {"baseTick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )}
   )
   private boolean redirectHurt(Entity instance, DamageSource damageSource, float damage, Operation<Boolean> original) {
      return FireManager.affect(
         instance, ((FireTyped)instance).getFireType(), Fire::getOnFire, (xva$0, xva$1, xva$2) -> (Boolean)original.call(new Object[]{xva$0, xva$1, xva$2})
      );
   }

   @WrapOperation(
      method = {"lavaHurt"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"
      )}
   )
   private void redirectSetSecondsOnFire(Entity instance, float seconds, Operation<Void> original) {
      FireManager.setOnFire(instance, seconds, FireManager.DEFAULT_FIRE_TYPE, (xva$0, xva$1) -> {
         Void var10000 = (Void)original.call(new Object[]{xva$0, xva$1});
      });
   }

   @Inject(
      method = {"setRemainingFireTicks"},
      at = {@At("HEAD")}
   )
   private void onSetRemainingFireTicks(int ticks, CallbackInfo ci) {
      if (!this.level.isClientSide && ticks >= this.getRemainingFireTicks()) {
         this.setFireType(FireManager.DEFAULT_FIRE_TYPE);
      }
   }

   @Inject(
      method = {"saveWithoutId"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
      )}
   )
   private void onSaveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
      tag.putString("FireType", FireManager.ensure(this.getFireType()).toString());
   }

   @Inject(
      method = {"load"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"
      )}
   )
   private void onLoad(CompoundTag tag, CallbackInfo ci) {
      this.setFireType(FireManager.ensure(ResourceLocation.tryParse(tag.getString("FireType"))));
   }
}
