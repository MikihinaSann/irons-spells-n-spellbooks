package net.alshanex.alshanex_familiars.entity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironsspellbooks.entity.mobs.HumanoidRenderer;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import net.alshanex.alshanex_familiars.entity.PlaguePetEntity;
import net.alshanex.alshanex_familiars.entity.models.PlaguePetModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class PlaguePetRenderer extends HumanoidRenderer<PlaguePetEntity> {
   public PlaguePetRenderer(Context renderManager, PlaguePetModel model) {
      super(renderManager, model);
      this.shadowRadius = 0.3F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }

   public ResourceLocation getTextureLocation(PlaguePetEntity entity) {
      return PlaguePetEntity.textureResource;
   }

   public void render(PlaguePetEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
      super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
      SpellRenderingHelper.renderSpellHelper(
         ClientMagicData.getSyncedSpellData((LivingEntity)this.animatable), (LivingEntity)this.animatable, poseStack, bufferSource, partialTick
      );
   }

   public RenderType getRenderType(PlaguePetEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return animatable.isInvisible() ? RenderType.entityTranslucent(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
   }
}
