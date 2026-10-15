package net.alshanex.alshanex_familiars.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.Map;
import net.alshanex.alshanex_familiars.AlshanexFamiliarsMod;
import net.alshanex.alshanex_familiars.util.FrozenEntityRenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class FrozenEntityRenderer extends EntityRenderer<FrozenEntity> {
   private final Map<String, EntityModel> modelMap = new HashMap<>();
   private final Map<String, Entity> hollowEntityMap = new HashMap<>();
   private final Context context;

   public FrozenEntityRenderer(Context context) {
      super(context);
      this.context = context;
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull FrozenEntity entity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }

   protected void preRenderCallback(FrozenEntity entity, PoseStack matrixStackIn, float partialTickTime) {
      float scale = entity.getScale() < 0.01F ? 1.0F : entity.getScale();
      matrixStackIn.scale(scale, scale, scale);
   }

   public void render(
      FrozenEntity entityToFreeze,
      float entityYaw,
      float partialTicks,
      @NotNull PoseStack matrixStackIn,
      @NotNull MultiBufferSource bufferIn,
      int packedLightIn
   ) {
      EntityModel model = new ZombieModel(this.context.bakeLayer(ModelLayers.ZOMBIE));
      if (this.modelMap.get(entityToFreeze.getFrozenEntityTypeString()) != null) {
         model = this.modelMap.get(entityToFreeze.getFrozenEntityTypeString());
      } else {
         Entity dummyEntity = entityToFreeze.getFrozenEntityType().create(Minecraft.getInstance().level);
         if (dummyEntity != null) {
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dummyEntity);
            if (renderer instanceof RenderLayerParent) {
               model = ((RenderLayerParent)renderer).getModel();
            }
         }

         this.modelMap.put(entityToFreeze.getFrozenEntityTypeString(), model);
      }

      if (model != null) {
         Entity fakeEntity = null;
         if (this.hollowEntityMap.get(entityToFreeze.getFrozenEntityTypeString()) == null) {
            Entity build = entityToFreeze.getFrozenEntityType().create(Minecraft.getInstance().level);
            if (build != null) {
               try {
                  build.load(entityToFreeze.getFrozenEntityTag());
               } catch (Exception var13) {
                  AlshanexFamiliarsMod.LOGGER.warn("Mob " + entityToFreeze.getFrozenEntityTypeString() + " could not build Frozen Entity");
               }

               fakeEntity = this.hollowEntityMap.putIfAbsent(entityToFreeze.getFrozenEntityTypeString(), build);
            }
         } else {
            fakeEntity = this.hollowEntityMap.get(entityToFreeze.getFrozenEntityTypeString());
         }

         RenderType tex = FrozenEntityRenderType.getFrozenEntityRenderType(200.0F, 200.0F);
         VertexConsumer ivertexbuilder = bufferIn.getBuffer(tex);
         matrixStackIn.pushPose();
         float yaw = entityToFreeze.yRotO + (entityToFreeze.getYRot() - entityToFreeze.yRotO) * partialTicks;
         boolean shouldSit = entityToFreeze.isPassenger() && entityToFreeze.getVehicle() != null && entityToFreeze.getVehicle().shouldRiderSit();
         model.young = entityToFreeze.isBaby();
         model.riding = shouldSit;
         model.attackTime = entityToFreeze.getAttackAnim(partialTicks);
         if (fakeEntity != null) {
            model.setupAnim(fakeEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
         }

         this.preRenderCallback(entityToFreeze, matrixStackIn, partialTicks);
         matrixStackIn.translate(0.0F, 1.5F, 0.0F);
         matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
         matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw));
         model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY);
         matrixStackIn.popPose();
      }
   }
}
