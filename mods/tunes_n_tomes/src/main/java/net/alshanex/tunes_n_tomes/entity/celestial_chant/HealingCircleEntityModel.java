package net.alshanex.tunes_n_tomes.entity.celestial_chant;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class HealingCircleEntityModel extends GeoModel<HealingCircleEntity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/healing_aura.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/healing_aura.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/healing_aura.png");
   protected TransformStack transformStack = new TransformStack();

   public ResourceLocation getModelResource(HealingCircleEntity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(HealingCircleEntity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(HealingCircleEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(HealingCircleEntity entity, long instanceId, AnimationState<HealingCircleEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused()) {
         this.transformStack.popStack();
      }
   }
}
