package net.alshanex.alshanex_familiars.entity.models;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.TransformStack;
import net.alshanex.alshanex_familiars.entity.ShadowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.WalkAnimationState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ShadowModel extends DefaultedEntityGeoModel<ShadowEntity> {
   public static final ResourceLocation MODEL = new ResourceLocation("alshanex_familiars", "geo/shadow_summon.geo.json");
   public static final ResourceLocation ANIMATIONS = new ResourceLocation("alshanex_familiars", "animations/shadow.animation.json");
   protected TransformStack transformStack = new TransformStack();

   public ShadowModel() {
      super(new ResourceLocation("alshanex_familiars", "shadow_summon"));
   }

   public ResourceLocation getModelResource(ShadowEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(ShadowEntity animatable) {
      return ANIMATIONS;
   }

   public void setCustomAnimations(ShadowEntity entity, long instanceId, AnimationState<ShadowEntity> animationState) {
      super.setCustomAnimations(entity, instanceId, animationState);
      if (!Minecraft.getInstance().isPaused()) {
         float partialTick = animationState.getPartialTick();
         GeoBone body = this.getAnimationProcessor().getBone("body");
         GeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
         GeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
         WalkAnimationState walkAnimationState = entity.walkAnimation;
         float pLimbSwingAmount = 0.0F;
         float pLimbSwing = 0.0F;
         if (entity.isAlive()) {
            pLimbSwingAmount = walkAnimationState.speed(partialTick);
            pLimbSwing = walkAnimationState.position(partialTick);
            if (entity.isBaby()) {
               pLimbSwing *= 3.0F;
            }

            if (pLimbSwingAmount > 1.0F) {
               pLimbSwingAmount = 1.0F;
            }
         }

         float f = 1.0F;
         if (entity.getFallFlyingTicks() > 4) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
         }

         if (f < 1.0F) {
            f = 1.0F;
         }

         this.transformStack.popStack();
      }
   }

   protected void bobBone(GeoBone bone, int offset, float multiplier) {
      float z = multiplier * (Mth.cos(offset * 0.09F) * 0.05F + 0.05F);
      float x = multiplier * Mth.sin(offset * 0.067F) * 0.05F;
      this.transformStack.pushRotation(bone, x, 0.0F, z);
   }
}
