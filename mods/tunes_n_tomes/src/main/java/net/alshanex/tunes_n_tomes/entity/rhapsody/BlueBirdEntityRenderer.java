package net.alshanex.tunes_n_tomes.entity.rhapsody;

import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BlueBirdEntityRenderer extends GeoLivingEntityRenderer<BlueBirdEntity> {
   public BlueBirdEntityRenderer(Context renderManager) {
      super(renderManager, new BlueBirdEntityModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
