package net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib;

import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.AscendedArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class AscendedArmorModel extends DefaultedItemGeoModel<AscendedArmorItem> {
   public AscendedArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", ""));
   }

   public ResourceLocation getModelResource(AscendedArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/ascended_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AscendedArmorItem object) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/geckolib/ascended_armor.png");
   }

   public ResourceLocation getAnimationResource(AscendedArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
