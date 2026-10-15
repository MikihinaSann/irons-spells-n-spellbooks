package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SoulEntityRenderer extends GeoLivingEntityRenderer<SoulEntity> {
   public SoulEntityRenderer(Context renderManager) {
      super(renderManager, new SoulEntityModel());
      this.shadowRadius = 0.0F;
      this.addRenderLayer(new AutoGlowingGeoLayer(this));
   }
}
