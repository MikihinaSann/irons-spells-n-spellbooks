package com.somake.somakespells.render;

import com.somake.somakespells.entity.SomakeGhostEntity;
import com.somake.somakespells.entity.model.SomakeGhostModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SomakeGhostRenderer extends GeoEntityRenderer<SomakeGhostEntity> {
   public SomakeGhostRenderer(Context renderManager) {
      super(renderManager, new SomakeGhostModel());
      this.shadowRadius = 0.35F;
   }

   public RenderType getRenderType(SomakeGhostEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucent(texture);
   }
}
