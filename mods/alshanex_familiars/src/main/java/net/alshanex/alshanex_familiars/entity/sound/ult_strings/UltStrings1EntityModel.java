package net.alshanex.alshanex_familiars.entity.sound.ult_strings;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class UltStrings1EntityModel extends GeoModel<UltStrings1Entity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/strings1.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/strings1.animation.json");
   public static final ResourceLocation TEXTURE = new ResourceLocation("alshanex_familiars", "textures/entity/ult_strings.png");
   protected TransformStack transformStack = new TransformStack();

   public ResourceLocation getModelResource(UltStrings1Entity object) {
      return MODEL;
   }

   public ResourceLocation getTextureResource(UltStrings1Entity mob) {
      return TEXTURE;
   }

   public ResourceLocation getAnimationResource(UltStrings1Entity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(UltStrings1Entity entity, long instanceId, AnimationState<UltStrings1Entity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused()) {
         this.transformStack.popStack();
      }
   }
}
