package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.FrostlingPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FrostlingPetModel extends DefaultedEntityGeoModel<FrostlingPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/frostling_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/frostling_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public FrostlingPetModel() {
      super(new ResourceLocation("alshanex_familiars", "frostling_pet"));
   }

   public ResourceLocation getModelResource(FrostlingPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(FrostlingPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(FrostlingPetEntity entity, long instanceId, AnimationState<FrostlingPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
