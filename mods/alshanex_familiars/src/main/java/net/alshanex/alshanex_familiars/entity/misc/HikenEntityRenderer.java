package net.alshanex.alshanex_familiars.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class HikenEntityRenderer extends EntityRenderer<Projectile> {
   public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(
      ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "fireball_model"), "main"
   );
   private static final ResourceLocation BASE_TEXTURE = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "textures/entity/empty.png");
   protected final ModelPart body;
   protected final ModelPart outline;
   protected final float scale;

   public HikenEntityRenderer(Context context, float scale) {
      super(context);
      ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
      this.body = modelpart.getChild("body");
      this.outline = modelpart.getChild("outline");
      this.scale = scale;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("outline", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 48, 24);
   }

   public void render(Projectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
      poseStack.pushPose();
      poseStack.translate(0.0, entity.getBoundingBox().getYsize() * 0.5, 0.0);
      poseStack.scale(this.scale, this.scale, this.scale);
      Vec3 motion = entity.getDeltaMovement();
      float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
      float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) + 90.0F);
      poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
      poseStack.mulPose(Axis.XP.rotationDegrees(xRot));
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
      this.body.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
      float f = entity.tickCount + partialTicks;
      poseStack.scale(1.15F, 1.15F, 1.15F);
      this.outline.render(poseStack, consumer, 15728880, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
   }

   public ResourceLocation getTextureLocation(Projectile entity) {
      return BASE_TEXTURE;
   }
}
