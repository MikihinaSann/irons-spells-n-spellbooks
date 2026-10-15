package net.hazen.hazennstuff.Item.Armor.Geckolib.BountyHunter;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BountyHunterArmorModel extends DefaultedEntityGeoModel<BountyHunterArmorItem> {
   public BountyHunterArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(BountyHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/bounty_hunter_armor.geo.json");
   }

   public ResourceLocation getTextureResource(BountyHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/bounty_hunter_armor.png");
   }

   public ResourceLocation getAnimationResource(BountyHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
