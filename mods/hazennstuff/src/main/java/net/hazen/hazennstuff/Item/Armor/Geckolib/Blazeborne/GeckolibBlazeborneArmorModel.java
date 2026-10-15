package net.hazen.hazennstuff.Item.Armor.Geckolib.Blazeborne;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibBlazeborneArmorModel extends DefaultedEntityGeoModel<GeckolibBlazeborneArmorItem> {
   public static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/blazeborne_armor.geo.json");

   public GeckolibBlazeborneArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibBlazeborneArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/blazeborne_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibBlazeborneArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/blazeborne_armor_alt.png");
   }

   public ResourceLocation getAnimationResource(GeckolibBlazeborneArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/blazeborne_armor.animation.json");
   }
}
