package net.alshanex.tunes_n_tomes.entity.red_ball;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RedBallEffectRenderer extends GeoEntityRenderer<RedBallEffectEntity> {
   public RedBallEffectRenderer(Context renderManager) {
      super(renderManager, new RedBallEffectModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
