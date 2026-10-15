package net.alshanex.alshanex_familiars.entity.sound;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.awt.Color;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MusicBoltRenderer extends EntityRenderer<MusicBolt> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("irons_spellbooks", "guiding_bolt_model"), "main");
   private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("alshanex_familiars", "textures/entity/music_bolt.png");
   protected final ModelPart body;
   protected final ModelPart outline;

   public MusicBoltRenderer(Context context) {
      super(context);
      ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
      this.body = modelpart.getChild("body");
      this.outline = modelpart.getChild("outline");
   }

   public void render(MusicBolt entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      int color = new Color(207, 255, 210).getRGB();
      poseStack.pushPose();
      Vec3 motion = entity.getDeltaMovement();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.energySwirl(this.getTextureLocation(entity), 0.0F, 0.0F));
      this.body.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, color);
      poseStack.scale(0.4F, 0.4F, 0.4F);
      this.outline.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY, color);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(MusicBolt entity) {
      return BASE_TEXTURE;
   }
}
