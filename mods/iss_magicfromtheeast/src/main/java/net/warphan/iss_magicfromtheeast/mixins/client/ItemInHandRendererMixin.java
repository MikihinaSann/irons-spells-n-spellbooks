package net.warphan.iss_magicfromtheeast.mixins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemInHandRenderer.HandRenderSelection;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.warphan.iss_magicfromtheeast.item.weapons.RepeatingCrossbow;
import net.warphan.iss_magicfromtheeast.registries.MFTEDataComponentRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin({ItemInHandRenderer.class})
public abstract class ItemInHandRendererMixin {
   @Shadow
   protected abstract void applyItemArmTransform(PoseStack var1, HumanoidArm var2, float var3);

   @Shadow
   protected abstract void applyItemArmAttackTransform(PoseStack var1, HumanoidArm var2, float var3);

   @Shadow
   public abstract void renderItem(LivingEntity var1, ItemStack var2, ItemDisplayContext var3, boolean var4, PoseStack var5, MultiBufferSource var6, int var7);

   @Inject(
      at = {@At("HEAD")},
      method = {"evaluateWhichHandsToRender"},
      cancellable = true
   )
   private static void S$evaluateWhichHandsToRender(LocalPlayer localPlayer, CallbackInfoReturnable<HandRenderSelection> callback) {
      ItemStack itemstack = localPlayer.getUseItem();
      ItemStack mainStack = localPlayer.getMainHandItem();
      ItemStack offStack = localPlayer.getOffhandItem();
      if (offStack.is(MFTEItemRegistries.REPEATING_CROSSBOW) && !mainStack.isEmpty()) {
         callback.setReturnValue(HandRenderSelection.RENDER_MAIN_HAND_ONLY);
      }

      if (itemstack.getItem() instanceof RepeatingCrossbow) {
         if (localPlayer.isUsingItem()) {
            InteractionHand interactionHand = localPlayer.getUsedItemHand();
            callback.setReturnValue(HandRenderSelection.onlyForHand(interactionHand));
         } else {
            callback.setReturnValue(
               itemstack.has(MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT)
                     && ((RepeatingCrossbow.ProjectileAmountComponent)itemstack.get(MFTEDataComponentRegistries.CROSSBOW_AMMO_AMOUNT)).ammoAmount() > 0
                  ? HandRenderSelection.RENDER_MAIN_HAND_ONLY
                  : HandRenderSelection.RENDER_BOTH_HANDS
            );
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"selectionUsingItemWhileHoldingBowLike"},
      cancellable = true
   )
   private static void S$selectionUsingItemWhileHoldingBowLike(LocalPlayer localPlayer, CallbackInfoReturnable<HandRenderSelection> callback) {
      ItemStack stack = localPlayer.getUseItem();
      InteractionHand interactionHand = localPlayer.getUsedItemHand();
      if (stack.getItem() instanceof RepeatingCrossbow) {
         callback.setReturnValue(HandRenderSelection.onlyForHand(interactionHand));
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderArmWithItem"},
      cancellable = true
   )
   private void renderRepeatingCrossbowItem(
      AbstractClientPlayer player,
      float v,
      float v1,
      InteractionHand hand,
      float v2,
      ItemStack stack,
      float v3,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int i0,
      CallbackInfo callback
   ) {
      if (!player.isScoping() && stack.getItem() instanceof RepeatingCrossbow) {
         if (player.getUseItem() == stack) {
            boolean flag = hand == InteractionHand.MAIN_HAND;
            HumanoidArm humanoidarm = flag ? player.getMainArm().getOpposite() : player.getMainArm();
            callback.cancel();
            poseStack.pushPose();
            boolean flag3 = RepeatingCrossbow.getAmmoAmount(stack) > 0;
            boolean flag2 = humanoidarm == HumanoidArm.LEFT;
            int i = flag2 ? 1 : -1;
            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
               this.applyItemArmTransform(poseStack, humanoidarm.getOpposite(), v3);
               poseStack.translate(i * -0.4785682F, -0.094387F, 0.05731531F);
               poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
               poseStack.mulPose(Axis.YP.rotationDegrees(i * 65.3F));
               poseStack.mulPose(Axis.ZP.rotationDegrees(i * -9.785F));
               float f12 = stack.getUseDuration(player) - (player.getUseItemRemainingTicks() - v + 1.0F);
               float f7 = f12 / RepeatingCrossbow.getUsingDuration(stack, player);
               if (f7 > 1.0F) {
                  f7 = 1.0F;
               }

               if (f7 > 0.1F) {
                  float f11 = Mth.sin((f12 - 0.1F) * 1.3F);
                  float f14 = f7 - 0.1F;
                  float f17 = f11 * f14;
                  poseStack.translate(f17 * 0.0F, f17 * 0.004F, f17 * 0.0F);
               }

               poseStack.translate(f7 * 0.0F, f7 * 0.0F, f7 * 0.04F);
               poseStack.scale(1.0F, 1.0F, 1.0F + f7 * 0.2F);
               poseStack.mulPose(Axis.YN.rotationDegrees(i * 45.0F));
            } else {
               float f12x = -0.4F * Mth.sin(Mth.sqrt(v2) * (float) Math.PI);
               float f7x = 0.2F * Mth.sin(Mth.sqrt(v2) * (float) (Math.PI * 2));
               float f11 = -0.2F * Mth.sin(v2 * (float) Math.PI);
               poseStack.translate(i * f12x, f7x, f11);
               this.applyItemArmTransform(poseStack, humanoidarm.getOpposite(), v3);
               this.applyItemArmAttackTransform(poseStack, humanoidarm.getOpposite(), v2);
               if (flag3 && v2 < 0.001F && flag) {
                  poseStack.translate(i * -0.641864F, 0.0F, 0.0F);
                  poseStack.mulPose(Axis.YP.rotationDegrees(i * 10.0F));
               }
            }

            this.renderItem(
               player,
               stack,
               flag2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
               !flag2,
               poseStack,
               bufferSource,
               i0
            );
            poseStack.popPose();
         } else {
            hand = InteractionHand.MAIN_HAND;
            HumanoidArm humanoidarm = player.getMainArm();
            callback.cancel();
            poseStack.pushPose();
            boolean flag3 = RepeatingCrossbow.getAmmoAmount(stack) > 0;
            boolean flag2 = humanoidarm == HumanoidArm.RIGHT;
            int i = flag2 ? 1 : -1;
            if (player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
               this.applyItemArmTransform(poseStack, humanoidarm, v3);
               poseStack.translate(i * -0.4785682F, -0.094387F, 0.05731531F);
               poseStack.mulPose(Axis.XP.rotationDegrees(-11.935F));
               poseStack.mulPose(Axis.YP.rotationDegrees(i * 65.3F));
               poseStack.mulPose(Axis.ZP.rotationDegrees(i * -9.785F));
               float f12x = stack.getUseDuration(player) - (player.getUseItemRemainingTicks() - v + 1.0F);
               float f7x = f12x / RepeatingCrossbow.getUsingDuration(stack, player);
               if (f7x > 1.0F) {
                  f7x = 1.0F;
               }

               if (f7x > 0.1F) {
                  float f11 = Mth.sin((f12x - 0.1F) * 1.3F);
                  float f14 = f7x - 0.1F;
                  float f17 = f11 * f14;
                  poseStack.translate(f17 * 0.0F, f17 * 0.004F, f17 * 0.0F);
               }

               poseStack.translate(f7x * 0.0F, f7x * 0.0F, f7x * 0.04F);
               poseStack.scale(1.0F, 1.0F, 1.0F + f7x * 0.2F);
               poseStack.mulPose(Axis.YN.rotationDegrees(i * 45.0F));
            } else {
               float f12xx = -0.4F * Mth.sin(Mth.sqrt(v2) * (float) Math.PI);
               float f7xx = 0.2F * Mth.sin(Mth.sqrt(v2) * (float) (Math.PI * 2));
               float f11 = -0.2F * Mth.sin(v2 * (float) Math.PI);
               poseStack.translate(i * f12xx, f7xx, f11);
               this.applyItemArmTransform(poseStack, humanoidarm, v3);
               this.applyItemArmAttackTransform(poseStack, humanoidarm, v2);
               if (flag3 && v2 < 0.001F) {
                  poseStack.translate(i * -0.641864F, 0.0F, 0.0F);
                  poseStack.mulPose(Axis.YP.rotationDegrees(i * 10.0F));
               }
            }

            this.renderItem(
               player,
               stack,
               flag2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
               !flag2,
               poseStack,
               bufferSource,
               i0
            );
            poseStack.popPose();
         }
      }
   }
}
