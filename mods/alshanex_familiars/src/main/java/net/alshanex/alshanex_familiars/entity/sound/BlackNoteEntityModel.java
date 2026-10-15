package net.alshanex.alshanex_familiars.entity.sound;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlackNoteEntityModel extends GeoModel<BlackNoteEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "textures/entity/black_strings.png");
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "geo/black_note.geo.json");
   public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath("alshanex_familiars", "animations/black_note.animation.json");

   public ResourceLocation getTextureResource(BlackNoteEntity object) {
      return TEXTURE;
   }

   public ResourceLocation getModelResource(BlackNoteEntity object) {
      return MODEL;
   }

   public ResourceLocation getAnimationResource(BlackNoteEntity animatable) {
      return ANIMS;
   }
}
