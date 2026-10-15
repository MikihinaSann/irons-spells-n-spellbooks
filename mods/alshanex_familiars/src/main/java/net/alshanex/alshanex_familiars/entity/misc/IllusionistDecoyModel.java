package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.IllusionistPetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IllusionistDecoyModel extends GeoModel<IllusionistDecoy> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/illusionist_pet.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/illusionist_pet.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public ResourceLocation getModelResource(IllusionistDecoy object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(IllusionistDecoy mob) {
      return IllusionistPetEntity.textureResource;
   }

   public ResourceLocation getAnimationResource(IllusionistDecoy animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(IllusionistDecoy entity, long instanceId, AnimationState<IllusionistDecoy> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused()) {
         this.transformStack.popStack();
      }
   }
}
