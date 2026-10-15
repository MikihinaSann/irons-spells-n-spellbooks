package net.hazen.hazennstuff.Item.Armor.Geckolib.Utau;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class UtauArmorModel extends DefaultedEntityGeoModel<UtauArmorItem> {
   public UtauArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(UtauArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/teto_kasane_utau_armor.geo.json");
   }

   public ResourceLocation getTextureResource(UtauArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/teto_kasane_utau_armor.png");
   }

   public ResourceLocation getAnimationResource(UtauArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
