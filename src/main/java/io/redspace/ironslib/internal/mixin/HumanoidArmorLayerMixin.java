package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.transmog.TransmogHolder;
import io.redspace.ironslib.transmog.TransmogItemData;
import io.redspace.ironslib.transmog.client.ISupportTransmog;
import io.redspace.ironslib.transmog.client.TransmogArmorRenderer;
import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.Color;

@Mixin({HumanoidArmorLayer.class})
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
   @Shadow
   protected abstract void setPartVisibility(A var1, EquipmentSlot var2);

   @WrapWithCondition(
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;)V"
      )}
   )
   public boolean irons_lib$handleTransmog(
      HumanoidArmorLayer<T, M, A> renderLayer,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      T entity,
      EquipmentSlot equipmentSlot,
      int packedLight,
      A baseModel,
      @Local(ordinal = 0, argsOnly = true) float limbSwing,
      @Local(ordinal = 1, argsOnly = true) float limbSwingAmount,
      @Local(ordinal = 2, argsOnly = true) float partialTick,
      @Local(ordinal = 4, argsOnly = true) float netHeadYaw,
      @Local(ordinal = 5, argsOnly = true) float headPitch
   ) {
      if (entity instanceof ISupportTransmog transmogProvider) {
         ItemStack stack = transmogProvider.irons_lib$getArmorBySlotRaw(equipmentSlot);
         if (stack.isEmpty()) {
            return true;
         } else {
            if (entity instanceof Player player && !TransmogClientHandler.canUseTransmog(player, stack)) {
               return true;
            }

            TransmogItemData itemData = TransmogItemData.get(stack);
            if (itemData == null) {
               return true;
            } else {
               TransmogHolder transmogHolder = itemData.transmog();
               if (transmogHolder == null) {
                  return true;
               } else {
                  TransmogArmorRenderer<?> geckolibModel = TransmogClientHandler.renderer(transmogHolder);
                  ((HumanoidModel)renderLayer.getParentModel()).copyPropertiesTo(baseModel);
                  this.setPartVisibility(baseModel, equipmentSlot);
                  geckolibModel.prepForRender(
                     entity, stack, equipmentSlot, baseModel, bufferSource, partialTick, limbSwing, limbSwingAmount, netHeadYaw, headPitch
                  );
                  baseModel.copyPropertiesTo(geckolibModel);
                  geckolibModel.renderToBuffer(poseStack, null, packedLight, OverlayTexture.NO_OVERLAY, Color.WHITE.argbInt());
                  if (stack.has((DataComponentType)GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get())) {
                     stack.set(
                        (DataComponentType)GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get(),
                        (Long)stack.get((DataComponentType)GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get())
                     );
                  }

                  return false;
               }
            }
         }
      } else {
         return true;
      }
   }
}
