package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class IllusionistDecoyRenderer extends GeoLivingEntityRenderer<IllusionistDecoy> {
   public IllusionistDecoyRenderer(Context renderManager) {
      super(renderManager, new IllusionistDecoyModel());
      this.shadowRadius = 0.3F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
