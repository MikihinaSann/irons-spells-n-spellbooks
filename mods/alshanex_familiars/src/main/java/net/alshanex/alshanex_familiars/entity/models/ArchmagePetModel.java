package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.ArchmagePetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ArchmagePetModel extends DefaultedEntityGeoModel<ArchmagePetEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/archmage_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/archmage_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public ArchmagePetModel() {
      super(new ResourceLocation("alshanex_familiars", "archmage_pet"));
   }

   public ResourceLocation getModelResource(ArchmagePetEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(ArchmagePetEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(ArchmagePetEntity entity, long instanceId, AnimationState<ArchmagePetEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused() && entity.shouldBeExtraAnimated()) {
         this.transformStack.popStack();
      }
   }
}
