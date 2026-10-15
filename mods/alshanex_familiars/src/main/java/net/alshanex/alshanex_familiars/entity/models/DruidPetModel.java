package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.DruidPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DruidPetModel extends DefaultedEntityGeoModel<DruidPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/druid_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/druid_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public DruidPetModel() {
      super(new ResourceLocation("alshanex_familiars", "druid_pet"));
   }

   public ResourceLocation getModelResource(DruidPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(DruidPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(DruidPetEntity entity, long instanceId, AnimationState<DruidPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
