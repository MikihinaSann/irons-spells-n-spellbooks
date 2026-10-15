package net.alshanex.tunes_n_tomes.entity.segno_marker;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SegnoMarkerRenderer extends GeoEntityRenderer<SegnoMarkerEntity> {
   public SegnoMarkerRenderer(Context context) {
      super(context, new SegnoMarkerModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public void render(SegnoMarkerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      if (entity.isResonating()) {
         this.renderAsOwner(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      } else {
         super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);
      }
   }

   private void renderAsOwner(
      SegnoMarkerEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight
   ) {
      UUID ownerUUID = entity.getOwnerUUID();
      if (ownerUUID != null) {
         if (entity.level().getPlayerByUUID(ownerUUID) instanceof AbstractClientPlayer owner) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            if (dispatcher.getRenderer(owner) instanceof PlayerRenderer playerRenderer) {
               poseStack.pushPose();
               float rotation = entity.getRotationAngle() + partialTick * 2.0F;
               poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
               playerRenderer.render(owner, entityYaw, partialTick, poseStack, bufferSource, packedLight);
               poseStack.popPose();
            }
         }
      }
   }

   public RenderType getRenderType(SegnoMarkerEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureLocation(SegnoMarkerEntity entity) {
      return ((SegnoMarkerModel)this.model).getTextureResource(entity);
   }
}
