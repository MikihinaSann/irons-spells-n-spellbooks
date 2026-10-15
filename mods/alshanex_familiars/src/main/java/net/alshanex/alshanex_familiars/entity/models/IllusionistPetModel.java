package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.IllusionistPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IllusionistPetModel extends DefaultedEntityGeoModel<IllusionistPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/illusionist_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/illusionist_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public IllusionistPetModel() {
      super(new ResourceLocation("alshanex_familiars", "illusionist_pet"));
   }

   public ResourceLocation getModelResource(IllusionistPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(IllusionistPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(IllusionistPetEntity entity, long instanceId, AnimationState<IllusionistPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
