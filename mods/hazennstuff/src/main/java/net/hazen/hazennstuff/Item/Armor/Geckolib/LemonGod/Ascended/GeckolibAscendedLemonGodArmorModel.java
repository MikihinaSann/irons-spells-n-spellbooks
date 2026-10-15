package net.hazen.hazennstuff.Item.Armor.Geckolib.LemonGod.Ascended;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibAscendedLemonGodArmorModel extends DefaultedEntityGeoModel<GeckolibAscendedLemonGodArmorItem> {
   public GeckolibAscendedLemonGodArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibAscendedLemonGodArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/lemon_god_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibAscendedLemonGodArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/lemon_god_armor.png");
   }

   public ResourceLocation getAnimationResource(GeckolibAscendedLemonGodArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
