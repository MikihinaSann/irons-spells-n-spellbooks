package net.alshanex.alshanex_familiars.entity.sound;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HealingCircleEntityRenderer extends GeoEntityRenderer<HealingCircleEntity> {
   public HealingCircleEntityRenderer(Context renderManager) {
      super(renderManager, new HealingCircleEntityModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
