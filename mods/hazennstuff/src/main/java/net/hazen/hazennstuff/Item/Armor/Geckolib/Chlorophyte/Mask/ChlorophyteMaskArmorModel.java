package net.hazen.hazennstuff.Item.Armor.Geckolib.Chlorophyte.Mask;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ChlorophyteMaskArmorModel extends DefaultedEntityGeoModel<ChlorophyteMaskArmorItem> {
   public ChlorophyteMaskArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ChlorophyteMaskArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/chlorophyte_armor.geo.json");
   }

   public ResourceLocation getTextureResource(ChlorophyteMaskArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/chlorophyte_mask_armor.png");
   }

   public ResourceLocation getAnimationResource(ChlorophyteMaskArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
