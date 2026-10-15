package net.alshanex.tunes_n_tomes.entity.blue_ball;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlueBallHitRenderer extends GeoEntityRenderer<BlueBallHitEntity> {
   public BlueBallHitRenderer(Context renderManager) {
      super(renderManager, new BlueBallHitModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
