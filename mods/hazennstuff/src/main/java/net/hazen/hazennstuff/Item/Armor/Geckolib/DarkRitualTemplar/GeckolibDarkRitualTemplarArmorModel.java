package net.hazen.hazennstuff.Item.Armor.Geckolib.DarkRitualTemplar;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GeckolibDarkRitualTemplarArmorModel extends DefaultedEntityGeoModel<GeckolibDarkRitualTemplarArmorItem> {
   public GeckolibDarkRitualTemplarArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GeckolibDarkRitualTemplarArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/dark_ritual_templar_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibDarkRitualTemplarArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/dark_ritual_templar_armor_geckolib.png");
   }

   public ResourceLocation getAnimationResource(GeckolibDarkRitualTemplarArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
