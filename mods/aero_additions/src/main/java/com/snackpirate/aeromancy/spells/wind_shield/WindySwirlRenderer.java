package com.snackpirate.aeromancy.spells.wind_shield;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class WindySwirlRenderer {
   private static final int COLOR = RenderHelper.colorf(0.5F, 0.5F, 0.5F);
   public static final long RENDER_ASPHYXIATION = 512L;

   private static RenderType getRenderType(ResourceLocation texture, float f) {
      return RenderType.energySwirl(texture, f * 0.02F % 1.0F, f * 0.01F % 1.0F);
   }

   private static boolean shouldRender(LivingEntity entity, Long shouldRenderFlag) {
      return shouldRenderFlag == 2L ? entity.hasEffect(AASpells.MobEffects.WIND_SHIELD) : entity.hasEffect(AASpells.MobEffects.BREATHLESS);
   }

   public static class Geo extends GeoRenderLayer<AbstractSpellCastingMob> {
      private final ResourceLocation TEXTURE;
      private final Long shouldRenderFlag;

      public Geo(GeoEntityRenderer<AbstractSpellCastingMob> entityRendererIn, ResourceLocation texture, Long shouldRenderFlag) {
         super(entityRendererIn);
         this.TEXTURE = texture;
         this.shouldRenderFlag = shouldRenderFlag;
      }

      public void render(
         PoseStack poseStack,
         AbstractSpellCastingMob animatable,
         BakedGeoModel bakedModel,
         RenderType renderType2,
         MultiBufferSource bufferSource,
         VertexConsumer buffer,
         float partialTick,
         int packedLight,
         int packedOverlay
      ) {
         if (WindySwirlRenderer.shouldRender(animatable, this.shouldRenderFlag)) {
            float f = animatable.tickCount + partialTick;
            RenderType renderType = WindySwirlRenderer.getRenderType(Aeromancy.id("textures/entity/wind_shield_layer.png"), f);
            VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            bakedModel.getBone("body").ifPresent(rootBone -> rootBone.getChildBones().forEach(bone -> bone.updateScale(1.1F, 1.1F, 1.1F)));
            this.getRenderer()
               .actuallyRender(
                  poseStack,
                  animatable,
                  bakedModel,
                  renderType,
                  bufferSource,
                  vertexconsumer,
                  true,
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  WindySwirlRenderer.COLOR
               );
            bakedModel.getBone("body").ifPresent(rootBone -> rootBone.getChildBones().forEach(bone -> bone.updateScale(1.0F, 1.0F, 1.0F)));
            poseStack.popPose();
         } else if (WindySwirlRenderer.shouldRender(animatable, this.shouldRenderFlag)) {
            float f = animatable.tickCount + partialTick;
            RenderType renderType = WindySwirlRenderer.getRenderType(Aeromancy.id("textures/entity/wind_shield_layer.png"), f);
            VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            bakedModel.getBone("head").ifPresent(bone -> bone.setHidden(true));
            bakedModel.getBone("body").ifPresent(rootBone -> rootBone.getChildBones().forEach(bone -> bone.updateScale(1.1F, 1.1F, 1.1F)));
            this.getRenderer()
               .actuallyRender(
                  poseStack,
                  animatable,
                  bakedModel,
                  renderType,
                  bufferSource,
                  vertexconsumer,
                  true,
                  partialTick,
                  packedLight,
                  OverlayTexture.NO_OVERLAY,
                  WindySwirlRenderer.COLOR
               );
            bakedModel.getBone("body").ifPresent(rootBone -> rootBone.getChildBones().forEach(bone -> bone.updateScale(1.0F, 1.0F, 1.0F)));
            poseStack.popPose();
         }
      }
   }

   public static class Vanilla extends RenderLayer<Player, HumanoidModel<Player>> {
      public static ModelLayerLocation LAYER = new ModelLayerLocation(IronsSpellbooks.id("energy_layer"), "main");
      private final HumanoidModel<Player> model = new HumanoidModel(Minecraft.getInstance().getEntityModels().bakeLayer(LAYER));
      private final ResourceLocation TEXTURE;
      private final Long shouldRenderFlag;

      public Vanilla(RenderLayerParent pRenderer, ResourceLocation texture, Long shouldRenderFlag) {
         super(pRenderer);
         this.TEXTURE = texture;
         this.shouldRenderFlag = shouldRenderFlag;
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         Player pLivingEntity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float pPartialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (WindySwirlRenderer.shouldRender(pLivingEntity, this.shouldRenderFlag)) {
            pMatrixStack.scale(1.1F, 1.0F, 1.1F);
            float f = pLivingEntity.tickCount + pPartialTicks;
            HumanoidModel<Player> entitymodel = this.model;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(WindySwirlRenderer.getRenderType(Aeromancy.id("textures/entity/wind_shield_layer.png"), f));
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(entitymodel);
            entitymodel.body.render(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, WindySwirlRenderer.COLOR);
            entitymodel.leftArm.render(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, WindySwirlRenderer.COLOR);
            entitymodel.rightArm.render(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, WindySwirlRenderer.COLOR);
            entitymodel.leftLeg.render(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, WindySwirlRenderer.COLOR);
            entitymodel.rightLeg.render(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, WindySwirlRenderer.COLOR);
         }

         if (WindySwirlRenderer.shouldRender(pLivingEntity, 512L)) {
            pMatrixStack.scale(1.1F, 1.0F, 1.1F);
            float f = pLivingEntity.tickCount + pPartialTicks;
            HumanoidModel<Player> entitymodel = this.model;
            VertexConsumer vertexconsumer = pBuffer.getBuffer(WindySwirlRenderer.getRenderType(Aeromancy.id("textures/entity/wind_shield_layer.png"), f));
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(entitymodel);
            entitymodel.head.render(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, WindySwirlRenderer.COLOR);
         }
      }

      protected HumanoidModel<Player> model() {
         return this.model;
      }
   }
}
