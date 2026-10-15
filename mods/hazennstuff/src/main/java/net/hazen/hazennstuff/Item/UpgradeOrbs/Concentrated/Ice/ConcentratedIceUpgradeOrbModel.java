package net.hazen.hazennstuff.Item.UpgradeOrbs.Concentrated.Ice;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ConcentratedIceUpgradeOrbModel extends DefaultedItemGeoModel<ConcentratedIceUpgradeOrb> {
   public ConcentratedIceUpgradeOrbModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ConcentratedIceUpgradeOrb animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/upgrade_orbs/concentrated_upgrade_orb_ice.geo.json");
   }

   public ResourceLocation getTextureResource(ConcentratedIceUpgradeOrb animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/upgrade_orbs/concentrated_upgrade_orb_ice.png");
   }

   public ResourceLocation getAnimationResource(ConcentratedIceUpgradeOrb animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/upgrade_orbs/concentrated_upgrade_orb_ice.animation.json");
   }
}
