package net.hazen.hazennstuff.Entity.Mobs.Mobs.VoidWanderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VoidWandererRenderer extends GeoEntityRenderer<VoidWanderer> {
   public VoidWandererRenderer(Context context) {
      super(context, new VoidWandererModel());
      this.addRenderLayer(new VoidWandererEmissiveLayer(this));
   }

   public void preRender(
      PoseStack poseStack,
      VoidWanderer entity,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTicks,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTicks, packedLight, packedOverlay, colour);
      poseStack.translate(0.0F, entity.getBbHeight() * 0.5F, 0.0F);
   }
}
