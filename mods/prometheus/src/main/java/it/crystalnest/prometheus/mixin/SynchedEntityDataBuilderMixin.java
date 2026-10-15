package it.crystalnest.prometheus.mixin;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTypeSynched;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Builder.class})
public abstract class SynchedEntityDataBuilderMixin {
   @Final
   @Shadow
   private SyncedDataHolder entity;

   @Shadow
   public abstract <T> Builder define(EntityDataAccessor<T> var1, T var2);

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onInit(SyncedDataHolder pEntity, CallbackInfo ci) {
      this.define(((FireTypeSynched)this.entity).fireTypeAccessor(), FireManager.DEFAULT_FIRE_TYPE.toString());
   }
}
