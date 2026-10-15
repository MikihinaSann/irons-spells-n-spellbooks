package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class PlaguePetModel extends DefaultedEntityGeoModel<PlaguePetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/plague_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/plague_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public PlaguePetModel() {
      super(new ResourceLocation("alshanex_familiars", "plague_pet"));
   }

   public ResourceLocation getModelResource(PlaguePetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(PlaguePetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(PlaguePetEntity entity, long instanceId, AnimationState<PlaguePetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
