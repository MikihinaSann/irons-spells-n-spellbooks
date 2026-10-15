package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.ScorcherPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ScorcherPetModel extends DefaultedEntityGeoModel<ScorcherPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/scorcher_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/scorcher_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public ScorcherPetModel() {
      super(new ResourceLocation("alshanex_familiars", "scorcher_pet"));
   }

   public ResourceLocation getModelResource(ScorcherPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(ScorcherPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(ScorcherPetEntity entity, long instanceId, AnimationState<ScorcherPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
