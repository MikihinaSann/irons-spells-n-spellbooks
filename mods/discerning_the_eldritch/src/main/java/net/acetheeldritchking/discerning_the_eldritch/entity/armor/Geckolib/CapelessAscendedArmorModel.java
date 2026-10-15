package net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib;

import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.CapelessAscendedArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class CapelessAscendedArmorModel extends DefaultedItemGeoModel<CapelessAscendedArmorItem> {
   public CapelessAscendedArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", ""));
   }

   public ResourceLocation getModelResource(CapelessAscendedArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/ascended_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CapelessAscendedArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/geckolib/ascended_armor.png");
   }

   public ResourceLocation getAnimationResource(CapelessAscendedArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
