package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.BardPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BardPetModel extends DefaultedEntityGeoModel<BardPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/bard_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/bard_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public BardPetModel() {
      super(new ResourceLocation("alshanex_familiars", "bard_pet"));
   }

   public ResourceLocation getModelResource(BardPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(BardPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(BardPetEntity entity, long instanceId, AnimationState<BardPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
