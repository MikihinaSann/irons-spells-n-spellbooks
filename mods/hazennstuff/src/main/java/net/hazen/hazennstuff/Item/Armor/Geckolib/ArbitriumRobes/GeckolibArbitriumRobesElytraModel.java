package net.hazen.hazennstuff.Item.Armor.Geckolib.ArbitriumRobes;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibArbitriumRobesElytraModel extends DefaultedEntityGeoModel<GeckolibArbitriumRobesElytraItem> {
   public GeckolibArbitriumRobesElytraModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibArbitriumRobesElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/arbitrium_robes_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibArbitriumRobesElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/arbitrium_robes_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibArbitriumRobesElytraItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/arbitrium_robes_armor.animation.json");
   }
}
