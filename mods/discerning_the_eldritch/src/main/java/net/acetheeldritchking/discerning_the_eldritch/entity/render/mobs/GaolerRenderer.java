package net.acetheeldritchking.discerning_the_eldritch.entity.render.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.gaoler.GaolerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class GaolerRenderer extends HumanoidRenderer<GaolerEntity> {
   public GaolerRenderer(Context renderManager, GaolerModel model) {
      super(renderManager, model);
      this.shadowRadius = 1.5F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public ResourceLocation getTextureLocation(GaolerEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/entity/gaoler/gaoler.png");
   }

   public void render(GaolerEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.scale(1.0F, 1.0F, 1.0F);
      super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
   }
}
