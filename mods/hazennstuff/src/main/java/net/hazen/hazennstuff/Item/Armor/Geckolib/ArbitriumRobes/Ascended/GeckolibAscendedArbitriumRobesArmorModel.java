package net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.Ascended;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibAscendedArbitriumRobesArmorModel extends DefaultedEntityGeoModel<GeckolibAscendedArbitriumRobesArmorItem> {
   public GeckolibAscendedArbitriumRobesArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibAscendedArbitriumRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/arbitrium_robes_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibAscendedArbitriumRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/arbitrium_robes_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibAscendedArbitriumRobesArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/arbitrium_robes_armor.animation.json");
   }
}
