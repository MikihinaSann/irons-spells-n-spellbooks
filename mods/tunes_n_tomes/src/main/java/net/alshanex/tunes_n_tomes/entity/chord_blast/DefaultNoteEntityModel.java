package net.alshanex.tunes_n_tomes.entity.chord_blast;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DefaultNoteEntityModel extends GeoModel<DefaultNoteEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "textures/entity/default_note.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "geo/default_note.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "animations/default_note.animation.json");

   public ResourceLocation getTextureResource(DefaultNoteEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(DefaultNoteEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(DefaultNoteEntity animatable) {
      return ANIMS;
   }
}
