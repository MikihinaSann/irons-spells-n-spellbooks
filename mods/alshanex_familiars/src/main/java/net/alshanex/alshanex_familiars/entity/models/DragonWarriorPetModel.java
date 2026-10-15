package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.DragonWarriorPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DragonWarriorPetModel extends DefaultedEntityGeoModel<DragonWarriorPetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/dragon_warrior_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/dragon_warrior_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public DragonWarriorPetModel() {
      super(new ResourceLocation("alshanex_familiars", "dragon_warrior_pet"));
   }

   public ResourceLocation getModelResource(DragonWarriorPetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(DragonWarriorPetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(DragonWarriorPetEntity entity, long instanceId, AnimationState<DragonWarriorPetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
