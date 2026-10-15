package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.HunterPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HunterPetModel extends DefaultedEntityGeoModel<HunterPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/hunter_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/hunter_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public HunterPetModel() {
      super(new ResourceLocation("alshanex_familiars", "hunter_pet"));
   }

   public ResourceLocation getModelResource(HunterPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(HunterPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(HunterPetEntity entity, long instanceId, AnimationState<HunterPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
