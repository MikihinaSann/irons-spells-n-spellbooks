package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.NecromancerPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NecromancerPetModel extends DefaultedEntityGeoModel<NecromancerPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/necromancer_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/necromancer_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public NecromancerPetModel() {
      super(new ResourceLocation("alshanex_familiars", "necromancer_pet"));
   }

   public ResourceLocation getModelResource(NecromancerPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(NecromancerPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(NecromancerPetEntity entity, long instanceId, AnimationState<NecromancerPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
