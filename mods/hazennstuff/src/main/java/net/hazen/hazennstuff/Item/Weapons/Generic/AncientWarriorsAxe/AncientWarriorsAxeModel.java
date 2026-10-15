package net.hazen.hazennstuff.Item.Weapons.Generic.AncientWarriorsAxe;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class AncientWarriorsAxeModel extends DefaultedItemGeoModel<AncientWarriorsAxeItem> {
   public AncientWarriorsAxeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AncientWarriorsAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/ancient_warriors_axe.geo.json");
   }

   public ResourceLocation getTextureResource(AncientWarriorsAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/ancient_warriors_axe.png");
   }

   public ResourceLocation getAnimationResource(AncientWarriorsAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
