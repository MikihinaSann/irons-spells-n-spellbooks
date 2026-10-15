package net.hazen.hazennstuff.Item.Armor.Geckolib.GarmentsOfTheFirstFlamebearer;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GarmentsOfTheFirstFlamebearerChestplateArmorModel extends DefaultedEntityGeoModel<GarmentsOfTheFirstFlamebearerChestplateArmorItem> {
   public GarmentsOfTheFirstFlamebearerChestplateArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(GarmentsOfTheFirstFlamebearerChestplateArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/garments_of_the_first_flamebearer_armor.geo.json");
   }

   public ResourceLocation getTextureResource(GarmentsOfTheFirstFlamebearerChestplateArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/garments_of_the_first_flamebearer_armor.png");
   }

   public ResourceLocation getAnimationResource(GarmentsOfTheFirstFlamebearerChestplateArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
