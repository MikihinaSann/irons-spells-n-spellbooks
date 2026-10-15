package net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes.Ascended;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibAscendedArbitriumRobesElytraArmorModel extends DefaultedEntityGeoModel<GeckolibAscendedArbitriumRobesElytraItem> {
   public GeckolibAscendedArbitriumRobesElytraArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibAscendedArbitriumRobesElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/arbitrium_robes_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibAscendedArbitriumRobesElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/arbitrium_robes_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibAscendedArbitriumRobesElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/arbitrium_robes_armor.animation.json");
   }
}
