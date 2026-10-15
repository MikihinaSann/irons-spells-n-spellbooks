package net.warphan.iss_magicfromtheeast.entity.spells.jade_judgement;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JadeDaoRenderer extends GeoEntityRenderer<JadeDao> {
   public JadeDaoRenderer(Context context) {
      super(context, new JadeDaoModel());
      this.shadowRadius = 2.0F;
   }

   public RenderType getRenderType(JadeDao animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
      return RenderType.energySwirl(texture, 0.0F, 0.0F);
   }
}
