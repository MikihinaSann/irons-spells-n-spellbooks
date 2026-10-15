package net.hazen.hazennstuff.Item.Armor.Geckolib.Seraph;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibSeraphArmorModel extends DefaultedEntityGeoModel<GeckolibSeraphArmorItem> {
   public GeckolibSeraphArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibSeraphArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/seraph_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibSeraphArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/seraph_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibSeraphArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/seraph_armor.animation.json");
   }
}
