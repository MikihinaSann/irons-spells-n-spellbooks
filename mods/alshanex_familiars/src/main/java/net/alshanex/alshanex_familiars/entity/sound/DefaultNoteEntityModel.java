package net.alshanex.alshanex_familiars.entity.sound;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DefaultNoteEntityModel extends GeoModel<DefaultNoteEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "textures/entity/default_note.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "geo/default_note.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "animations/default_note.animation.json");

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
