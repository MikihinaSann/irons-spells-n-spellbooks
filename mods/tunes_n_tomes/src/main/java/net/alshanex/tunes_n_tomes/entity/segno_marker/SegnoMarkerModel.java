package net.alshanex.tunes_n_tomes.entity.segno_marker;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SegnoMarkerModel extends GeoModel<SegnoMarkerEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/segno_base.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/segno_base.geo.json");
   private static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/segno_marker.animation.json");

   public ResourceLocation getTextureResource(SegnoMarkerEntity entity) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(SegnoMarkerEntity entity) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(SegnoMarkerEntity entity) {
      return ANIMS;
   }
}
