package net.acetheeldritchking.discerning_the_eldritch.entity.armor.Geckolib;

import net.acetheeldritchking.discerning_the_eldritch.items.armor.Geckolib.GeckolibCrimsonStagArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class GeckolibCrimsonStagArmorModel extends DefaultedItemGeoModel<GeckolibCrimsonStagArmorItem> {
   public GeckolibCrimsonStagArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", ""));
   }

   public ResourceLocation getModelResource(GeckolibCrimsonStagArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/crimson_stag_geckolib.geo.json");
   }

   public ResourceLocation getTextureResource(GeckolibCrimsonStagArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/geckolib/crimson_stag_gecko.png");
   }

   public ResourceLocation getAnimationResource(GeckolibCrimsonStagArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
