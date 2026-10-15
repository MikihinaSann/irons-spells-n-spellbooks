package net.alshanex.tunes_n_tomes.entity.ult_strings;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class UltStrings4EntityModel extends GeoModel<UltStrings4Entity> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/strings4.geo.json");
   public static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/strings4.animation.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/ult_strings.png");
   protected TransformStack transformStack = new TransformStack();

   public ResourceLocation getModelResource(UltStrings4Entity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(UltStrings4Entity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(UltStrings4Entity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(UltStrings4Entity entity, long instanceId, AnimationState<UltStrings4Entity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused()) {
         this.transformStack.popStack();
      }
   }
}
