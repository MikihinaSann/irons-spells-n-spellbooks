package net.alshanex.tunes_n_tomes.entity.blue_ball;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlueBallEffectRenderer extends GeoEntityRenderer<BlueBallEffectEntity> {
   public BlueBallEffectRenderer(Context renderManager) {
      super(renderManager, new BlueBallEffectModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
