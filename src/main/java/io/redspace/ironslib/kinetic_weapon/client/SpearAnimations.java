package io.redspace.ironslib.kinetic_weapon.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironslib.kinetic_weapon.KineticAnimation;
import io.redspace.ironslib.kinetic_weapon.KineticWeapon;
import io.redspace.ironslib.util.Ease;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class SpearAnimations implements KineticAnimation {
   public static final SpearAnimations INSTANCE = new SpearAnimations();

   private static float progress(float time, float start, float end) {
      return Mth.clamp(Mth.inverseLerp(time, start, end), 0.0F, 1.0F);
   }

   public static float hitFeedbackAmount(float ticksSinceFeedbackStart) {
      return 0.4F * (Ease.outQuart(progress(ticksSinceFeedbackStart, 1.0F, 3.0F)) - Ease.inOutSine(progress(ticksSinceFeedbackStart, 3.0F, 10.0F)));
   }

   @Override
   public void firstPersonUse(float ticksSinceKineticHitFeedback, PoseStack poseStack, float timeHeld, HumanoidArm arm, ItemStack itemStack) {
      KineticWeapon kineticWeapon = KineticWeapon.get(itemStack);
      if (kineticWeapon != null) {
         SpearAnimations.UseParams params = SpearAnimations.UseParams.fromKineticWeapon(kineticWeapon, timeHeld);
         int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
         poseStack.translate(
            (double)(
               (float)invert
                  * (params.raiseProgress() * 0.15F + params.raiseProgressEnd() * -0.05F + params.swayProgress() * -0.1F + params.swayScaleSlow() * 0.005F)
            ),
            (double)(params.raiseProgress() * -0.075F + params.raiseProgressMiddle() * 0.075F + params.swayScaleFast() * 0.01F),
            (double)params.raiseProgressStart() * 0.05 + (double)params.raiseProgressEnd() * -0.05 + (double)(params.swayScaleSlow() * 0.005F)
         );
         poseStack.rotateAround(
            Axis.XP
               .rotationDegrees(
                  -65.0F * Ease.inOutBack(params.raiseProgress())
                     - 35.0F * params.lowerProgress()
                     + 100.0F * params.raiseBackProgress()
                     + -0.5F * params.swayScaleFast()
               ),
            0.0F,
            0.1F,
            0.0F
         );
         poseStack.rotateAround(
            Axis.YN
               .rotationDegrees(
                  (float)invert * (-90.0F * progress(params.raiseProgress(), 0.5F, 0.55F) + 90.0F * params.swayProgress() + 2.0F * params.swayScaleSlow())
               ),
            (float)invert * 0.15F,
            0.0F,
            0.0F
         );
         poseStack.translate(0.0F, -hitFeedbackAmount(ticksSinceKineticHitFeedback), 0.0F);
      }
   }

   @Override
   public void thirdPersonArmPose(HumanoidModel<?> model, LivingEntity entity, HumanoidArm side, float ticksUsingItem, ItemStack itemStack) {
      KineticWeapon kineticWeapon = KineticWeapon.get(itemStack);
      if (kineticWeapon != null) {
         boolean holdingInRightArm = side == HumanoidArm.RIGHT;
         ModelPart arm = holdingInRightArm ? model.rightArm : model.leftArm;
         ModelPart head = model.head;
         int invert = holdingInRightArm ? 1 : -1;
         arm.yRot = -0.1F * (float)invert + head.yRot;
         arm.xRot = (float) (-Math.PI / 2) + head.xRot + 0.8F;
         if (entity.getFallFlyingTicks() > 4 || model.swimAmount > 0.0F) {
            arm.xRot -= 0.9599311F;
         }

         arm.yRot = (float) (Math.PI / 180.0) * Mth.clamp(arm.yRot * (180.0F / (float)Math.PI), -60.0F, 60.0F);
         arm.xRot = (float) (Math.PI / 180.0) * Mth.clamp(arm.xRot * (180.0F / (float)Math.PI), -120.0F, 30.0F);
         if (!(ticksUsingItem <= 0.0F)) {
            SpearAnimations.UseParams params = SpearAnimations.UseParams.fromKineticWeapon(kineticWeapon, ticksUsingItem);
            arm.yRot = arm.yRot + (float)(-invert) * params.swayScaleFast() * (float) (Math.PI / 180.0) * params.swayIntensity();
            arm.zRot = arm.zRot + (float)(-invert) * params.swayScaleSlow() * (float) (Math.PI / 180.0) * params.swayIntensity() * 0.5F;
            arm.xRot = arm.xRot
               + (float) (Math.PI / 180.0)
                  * (
                     -40.0F * params.raiseProgressStart()
                        + 30.0F * params.raiseProgressMiddle()
                        + -20.0F * params.raiseProgressEnd()
                        + 20.0F * params.lowerProgress()
                        + 10.0F * params.raiseBackProgress()
                        + 0.6F * params.swayScaleSlow() * params.swayIntensity()
                  );
         }
      }
   }

   @Override
   public void thirdPersonItem(PoseStack poseStack, LivingEntity entity, HumanoidArm arm, float ticksUsingItem, float partialTick, ItemStack itemStack) {
      KineticWeapon kineticWeapon = KineticWeapon.get(itemStack);
      if (kineticWeapon != null && ticksUsingItem != 0.0F) {
         float attackTime = entity.getAttackAnim(partialTick);
         float attack = Ease.inQuad(progress(attackTime, 0.05F, 0.2F));
         float retract = Ease.inOutExpo(progress(attackTime, 0.4F, 1.0F));
         SpearAnimations.UseParams params = SpearAnimations.UseParams.fromKineticWeapon(kineticWeapon, ticksUsingItem);
         int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
         float raiseProgressModified = 1.0F - Ease.outBack(1.0F - params.raiseProgress());
         float hitFeedback = hitFeedbackAmount(KineticHitFeedback.ticksSinceHit(entity, partialTick));
         poseStack.translate(
            0.0, (double)(-hitFeedback) * 0.4, (double)(-kineticWeapon.forwardMovement() * (raiseProgressModified - params.raiseBackProgress()) + hitFeedback)
         );
         poseStack.rotateAround(
            Axis.XN.rotationDegrees(70.0F * (params.raiseProgress() - params.raiseBackProgress()) - 40.0F * (attack - retract)), 0.0F, -0.03125F, 0.125F
         );
         poseStack.rotateAround(
            Axis.YP.rotationDegrees((float)(invert * 90) * (params.raiseProgress() - params.swayProgress() + 3.0F * retract + attack)), 0.0F, 0.0F, 0.125F
         );
      }
   }

   public static record UseParams(
      float raiseProgress,
      float raiseProgressStart,
      float raiseProgressMiddle,
      float raiseProgressEnd,
      float swayProgress,
      float lowerProgress,
      float raiseBackProgress,
      float swayIntensity,
      float swayScaleSlow,
      float swayScaleFast
   ) {
      public static SpearAnimations.UseParams fromKineticWeapon(KineticWeapon kineticWeapon, float time) {
         int finishRaisingTick = kineticWeapon.delayTicks();
         int finishSwayingTick = kineticWeapon.dismountConditions().map(KineticWeapon.Condition::maxDurationTicks).orElse(0) + finishRaisingTick;
         int startSwayingTick = finishSwayingTick - 20;
         int finishLoweringTick = kineticWeapon.knockbackConditions().map(KineticWeapon.Condition::maxDurationTicks).orElse(0) + finishRaisingTick;
         int startLoweringTick = finishLoweringTick - 40;
         int finishRaisingBackTick = kineticWeapon.damageConditions().map(KineticWeapon.Condition::maxDurationTicks).orElse(0) + finishRaisingTick;
         float raiseProgress = SpearAnimations.progress(time, 0.0F, (float)finishRaisingTick);
         float raiseProgressStart = SpearAnimations.progress(raiseProgress, 0.0F, 0.5F);
         float raiseProgressMiddle = SpearAnimations.progress(raiseProgress, 0.5F, 0.8F);
         float raiseProgressEnd = SpearAnimations.progress(raiseProgress, 0.8F, 1.0F);
         float swayProgress = SpearAnimations.progress(time, (float)startSwayingTick, (float)finishSwayingTick);
         float lowerProgress = Ease.outCubic(Ease.inOutElastic(SpearAnimations.progress(time - 20.0F, (float)startLoweringTick, (float)finishLoweringTick)));
         float raiseBackProgress = SpearAnimations.progress(time, (float)(finishRaisingBackTick - 5), (float)finishRaisingBackTick);
         float swayIntensity = 2.0F * Ease.outCirc(swayProgress) - 2.0F * Ease.inCirc(raiseBackProgress);
         float swayScaleSlow = Mth.sin(time * 19.0F * (float) (Math.PI / 180.0)) * swayIntensity;
         float swayScaleFast = Mth.sin(time * 30.0F * (float) (Math.PI / 180.0)) * swayIntensity;
         return new SpearAnimations.UseParams(
            raiseProgress,
            raiseProgressStart,
            raiseProgressMiddle,
            raiseProgressEnd,
            swayProgress,
            lowerProgress,
            raiseBackProgress,
            swayIntensity,
            swayScaleSlow,
            swayScaleFast
         );
      }
   }
}
