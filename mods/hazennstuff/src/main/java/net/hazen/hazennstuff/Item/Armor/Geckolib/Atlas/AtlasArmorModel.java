package net.hazen.hazennstuff.Item.Armor.Geckolib.Atlas;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AtlasArmorModel extends DefaultedEntityGeoModel<AtlasArmorItem> {
   public AtlasArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AtlasArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/atlas_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AtlasArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/atlas_armor.png");
   }

   public ResourceLocation getAnimationResource(AtlasArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/atlas_armor.animation.json");
   }
}
