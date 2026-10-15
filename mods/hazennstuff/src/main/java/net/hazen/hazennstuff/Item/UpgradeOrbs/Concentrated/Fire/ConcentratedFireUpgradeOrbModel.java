package net.hazen.hazennstuff.Item.UpgradeOrbs.Concentrated.Fire;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ConcentratedFireUpgradeOrbModel extends DefaultedItemGeoModel<ConcentratedFireUpgradeOrb> {
   public ConcentratedFireUpgradeOrbModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ConcentratedFireUpgradeOrb animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/upgrade_orbs/concentrated_upgrade_orb_core.geo.json");
   }

   public ResourceLocation getTextureResource(ConcentratedFireUpgradeOrb animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/upgrade_orbs/concentrated_upgrade_orb_fire.png");
   }

   public ResourceLocation getAnimationResource(ConcentratedFireUpgradeOrb animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/upgrade_orbs/concentrated_upgrade_orb.animation.json");
   }
}
