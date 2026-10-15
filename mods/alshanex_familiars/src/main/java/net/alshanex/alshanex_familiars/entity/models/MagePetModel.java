package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.MagePetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagePetModel extends DefaultedEntityGeoModel<MagePetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/mage_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/mage_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public MagePetModel() {
      super(new ResourceLocation("alshanex_familiars", "mage_pet"));
   }

   public ResourceLocation getModelResource(MagePetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(MagePetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(MagePetEntity entity, long instanceId, AnimationState<MagePetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
