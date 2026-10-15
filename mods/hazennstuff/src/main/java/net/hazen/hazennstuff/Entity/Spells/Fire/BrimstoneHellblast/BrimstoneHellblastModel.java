package net.hazen.hazennstuff.Entity.Spells.Fire.BrimstoneHellblast;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BrimstoneHellblastModel extends GeoModel<BrimstoneHellblast> {
   public ResourceLocation getModelResource(BrimstoneHellblast animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/entities/spells/brimstone_hellblast.geo.json");
   }

   public ResourceLocation getTextureResource(BrimstoneHellblast animatable) {
      int frameCount = 5;
      int frameDuration = 2;
      int frame = animatable.tickCount / frameDuration % frameCount;
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/spells/brimstone_hellblast_" + frame + ".png");
   }

   public ResourceLocation getAnimationResource(BrimstoneHellblast animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/entities/spells/brimstone_hellblast.animation.json");
   }
}
