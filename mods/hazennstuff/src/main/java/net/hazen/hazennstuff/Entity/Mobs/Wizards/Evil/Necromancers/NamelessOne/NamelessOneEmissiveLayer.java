package net.hazen.hazennstuff.Entity.Mobs.Wizards.Evil.Necromancers.NamelessOne;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class NamelessOneEmissiveLayer extends GeoRenderLayer<AbstractSpellCastingMob> {
   public NamelessOneEmissiveLayer(GeoEntityRenderer<AbstractSpellCastingMob> renderer) {
      super(renderer);
   }

   private static RenderType glowRenderType(ResourceLocation texture) {
      return RenderType.entityCutout(texture);
   }

   public void render(
      PoseStack poseStack,
      AbstractSpellCastingMob animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (!animatable.isInvisible()) {
         ResourceLocation frameTexture = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/mobs/nameless_one_glowmask.png");
         RenderType emissiveType = glowRenderType(frameTexture);
         VertexConsumer vertexConsumer = bufferSource.getBuffer(emissiveType);
         poseStack.pushPose();
         this.getRenderer()
            .actuallyRender(
               poseStack, animatable, bakedModel, emissiveType, bufferSource, vertexConsumer, true, partialTick, 15728880, OverlayTexture.NO_OVERLAY, -1
            );
         poseStack.popPose();
      }
   }
}
