package net.alshanex.tunes_n_tomes.entity.red_arrow;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RedArrowRenderer extends GeoEntityRenderer<RedArrowEntity> {
   public RedArrowRenderer(Context renderManager) {
      super(renderManager, new RedArrowModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
