package net.acetheeldritchking.discerning_the_eldritch.entity.spells.ravenous_jaw;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RavenousJawRenderer extends GeoEntityRenderer<RavenousJawEntity> {
   public RavenousJawRenderer(Context renderManager) {
      super(renderManager, new RavenousJawModel());
   }

   public boolean shouldShowName(RavenousJawEntity animatable) {
      return false;
   }
}
