package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.ClericPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ClericPetModel extends DefaultedEntityGeoModel<ClericPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/cleric_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/cleric_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public ClericPetModel() {
      super(new ResourceLocation("alshanex_familiars", "cleric_pet"));
   }

   public ResourceLocation getModelResource(ClericPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(ClericPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(ClericPetEntity entity, long instanceId, AnimationState<ClericPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
