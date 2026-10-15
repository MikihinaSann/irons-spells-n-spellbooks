package com.chadate.spellelemental.client.render;

import com.chadate.spellelemental.client.network.custom.ClientPayloadHandler;
import com.chadate.spellelemental.data.ElementContainerAttachment;
import com.chadate.spellelemental.data.SpellAttachments;
import com.chadate.spellelemental.element.attachment.data.UnifiedElementAttachmentAssets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent.Post;
import org.joml.Matrix4f;

@EventBusSubscriber(
   modid = "spellelemental",
   value = {Dist.CLIENT}
)
public class ElementIconRenderer {
   @SubscribeEvent
   public static void onRenderLivingPost(Post<LivingEntity, ?> event) {
      LivingEntity entity = event.getEntity();
      ElementContainerAttachment container = (ElementContainerAttachment)entity.getData(SpellAttachments.ELEMENTS_CONTAINER);
      Map<String, Integer> snapshot = container.snapshot();
      if (!snapshot.isEmpty()) {
         ElementIconRenderConfig cfg = ElementIconRenderConfig.get();
         List<String> elementKeys = new ArrayList<>(snapshot.keySet());
         int count = elementKeys.size();
         PoseStack poseStack = event.getPoseStack();
         MultiBufferSource buffer = event.getMultiBufferSource();
         poseStack.pushPose();
         poseStack.translate(0.0, entity.getBbHeight() + cfg.getVerticalOffset(), 0.0);
         if (cfg.isFaceCamera()) {
            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            if (cfg.isFlipBillboard()) {
               poseStack.scale(-1.0F, -1.0F, 1.0F);
            }
         }

         double totalWidth = (count - 1) * cfg.getHorizontalSpacing();
         double startX = -totalWidth / 2.0;

         for (int i = 0; i < count; i++) {
            String elementKey = elementKeys.get(i);
            String iconPath = UnifiedElementAttachmentAssets.getIcon(elementKey);
            if (iconPath != null) {
               poseStack.pushPose();
               poseStack.translate(startX + i * cfg.getHorizontalSpacing(), 0.0, 0.0);
               poseStack.scale(cfg.getQuadScale(), cfg.getQuadScale(), cfg.getQuadScale());
               ResourceLocation rl = resolveTexture(iconPath);
               if (rl != null) {
                  Matrix4f matrix = poseStack.last().pose();
                  VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(rl));
                  float z = cfg.getZDepth();
                  int remain = ClientPayloadHandler.DisplayCache.predictRemaining(entity.getId(), elementKey);
                  float a = computeAlpha(remain, cfg);
                  if (a <= 0.0F) {
                     poseStack.popPose();
                     continue;
                  }

                  float[][] vertices = new float[][]{
                     {-1.0F, 1.0F, 0.0F, 0.0F}, {1.0F, 1.0F, 1.0F, 0.0F}, {1.0F, -1.0F, 1.0F, 1.0F}, {-1.0F, -1.0F, 0.0F, 1.0F}
                  };

                  for (float[] v : vertices) {
                     vc.addVertex(matrix, v[0], v[1], z)
                        .setColor(1.0F, 1.0F, 1.0F, a)
                        .setUv(v[2], v[3])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(event.getPackedLight())
                        .setNormal(0.0F, 1.0F, 0.0F);
                  }
               }

               poseStack.popPose();
            }
         }

         poseStack.popPose();
      }
   }

   private static float computeAlpha(int remain, ElementIconRenderConfig cfg) {
      if (remain < 0) {
         return cfg.getAlpha();
      } else {
         int th = cfg.getFlashingThreshold();
         if (remain > th) {
            return cfg.getAlpha();
         } else {
            float t = Math.max(0.0F, Math.min(1.0F, (float)remain / th));
            float baseAlpha = cfg.getMinAlpha() + (cfg.getMaxAlpha() - cfg.getMinAlpha()) * t;
            float hz = cfg.getMinFlashHz() + (cfg.getMaxFlashHz() - cfg.getMinFlashHz()) * (1.0F - t);
            float timeSec = (float)(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getGameTime() : 0L) / 20.0F;
            float phase = (float)Math.sin((Math.PI * 2) * hz * timeSec);
            float flicker = 0.5F * (phase * 0.5F + 0.5F);
            return Math.max(0.0F, Math.min(1.0F, baseAlpha - flicker));
         }
      }
   }

   private static ResourceLocation resolveTexture(String iconPath) {
      try {
         Minecraft mc = Minecraft.getInstance();
         if (iconPath != null && !iconPath.isEmpty()) {
            ResourceLocation rl = ResourceLocation.parse(iconPath);
            Optional<Resource> res = mc.getResourceManager().getResource(rl);
            if (res.isPresent()) {
               return rl;
            }

            String candidate = rl.getPath();
            if (candidate.startsWith("textures/")) {
               candidate = candidate.substring("textures/".length());
            }

            if (candidate.endsWith(".png")) {
               candidate = candidate.substring(0, candidate.length() - 4);
            }

            ResourceLocation rl2 = ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "textures/" + candidate + ".png");
            if (mc.getResourceManager().getResource(rl2).isPresent()) {
               return rl2;
            }
         }
      } catch (Exception var6) {
      }

      return null;
   }
}
