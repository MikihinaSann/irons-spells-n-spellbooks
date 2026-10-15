package net.hazen.hazennstuff.Item.Armor.Geckolib.LemonGod;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibLemonGodArmorModel extends DefaultedEntityGeoModel<GeckolibLemonGodArmorItem> {
   public GeckolibLemonGodArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibLemonGodArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/lemon_god_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibLemonGodArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/lemon_god_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibLemonGodArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
