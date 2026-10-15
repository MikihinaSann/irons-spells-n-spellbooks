package com.somake.somakespells.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.somake.somakespells.block.entity.SoulFireAltarState;
import com.somake.somakespells.ritual.RitualElementalMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"com.github.L_Ender.cataclysm.client.render.blockentity.RendererAltar_of_Fire"}
)
public abstract class RendererAltarOfFireMixin<T extends AltarOfFire_Block_Entity> {
   private static final ResourceLocation SOMAKE$SOUL_FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/block/altar_of_soulfire.png"
   );
   private static final ResourceLocation SOMAKE$INFERNAL_FIRE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "somakespells", "textures/block/altar_of_infernalfire.png"
   );
   private static final String CATACLYSM_ALTAR_FIRE_PREFIX = "textures/block/altar_of_fire/altar_fire_";
   private static final String SOMAKE_SOUL_FIRE_PREFIX = "textures/block/altar_fire_soulfire_";
   private static final String SOMAKE_INFERNAL_FIRE_PREFIX = "textures/block/altar_fire_infernalfire_";
   @Unique
   private static final ThreadLocal<RitualElementalMode> SOMAKE$CURRENT_RITUAL_MODE = ThreadLocal.withInitial(() -> RitualElementalMode.NORMAL);

   @Inject(
      method = {"render"},
      at = {@At("HEAD")}
   )
   private void somake$captureRitualMode(
      T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, CallbackInfo ci
   ) {
      if (tileEntityIn instanceof SoulFireAltarState soulFireState) {
         SOMAKE$CURRENT_RITUAL_MODE.set(soulFireState.somake$getRitualMode());
      } else {
         SOMAKE$CURRENT_RITUAL_MODE.set(RitualElementalMode.NORMAL);
      }
   }

   @Inject(
      method = {"render"},
      at = {@At("RETURN")}
   )
   private void somake$clearRitualMode(
      T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, CallbackInfo ci
   ) {
      SOMAKE$CURRENT_RITUAL_MODE.remove();
   }

   @Redirect(
      method = {"render"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/RenderType;entityCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
      ),
      require = 0
   )
   private RenderType somake$swapSoulFireBaseTexture(
      ResourceLocation originalTexture,
      T tileEntityIn,
      float partialTicks,
      PoseStack matrixStackIn,
      MultiBufferSource bufferIn,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      return RenderType.entityCutoutNoCull(this.somake$getTexture(originalTexture, tileEntityIn));
   }

   @Redirect(
      method = {"renderFlamePart"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/RenderType;beaconBeam(Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/client/renderer/RenderType;"
      ),
      require = 0
   )
   private RenderType somake$swapSoulFireAnimatedTexture(
      ResourceLocation originalTexture, boolean colorFlag, float ticks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
   ) {
      return RenderType.beaconBeam(this.somake$getAnimatedTexture(originalTexture), colorFlag);
   }

   private ResourceLocation somake$getTexture(ResourceLocation originalTexture, T tileEntityIn) {
      if (tileEntityIn instanceof SoulFireAltarState soulFireState) {
         return switch (soulFireState.somake$getRitualMode()) {
            case SOUL_FIRE -> SOMAKE$SOUL_FIRE_TEXTURE;
            case INFERNAL_FIRE -> SOMAKE$INFERNAL_FIRE_TEXTURE;
            default -> originalTexture;
         };
      } else {
         return originalTexture;
      }
   }

   private ResourceLocation somake$getAnimatedTexture(ResourceLocation originalTexture) {
      String path = originalTexture.getPath();
      if (!path.startsWith("textures/block/altar_of_fire/altar_fire_")) {
         return originalTexture;
      } else {
         String suffix = path.substring("textures/block/altar_of_fire/altar_fire_".length());
         if (SOMAKE$CURRENT_RITUAL_MODE.get() == RitualElementalMode.SOUL_FIRE) {
            return ResourceLocation.fromNamespaceAndPath("somakespells", "textures/block/altar_fire_soulfire_" + suffix);
         } else {
            return SOMAKE$CURRENT_RITUAL_MODE.get() == RitualElementalMode.INFERNAL_FIRE
               ? ResourceLocation.fromNamespaceAndPath("somakespells", "textures/block/altar_fire_infernalfire_" + suffix)
               : originalTexture;
         }
      }
   }
}
