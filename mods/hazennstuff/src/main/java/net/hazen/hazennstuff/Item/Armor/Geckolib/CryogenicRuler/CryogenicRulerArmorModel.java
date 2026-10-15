package net.hazen.hazennstuff.Item.Armor.Geckolib.CryogenicRuler;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CryogenicRulerArmorModel extends DefaultedEntityGeoModel<CryogenicRulerArmorItem> {
   public CryogenicRulerArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(CryogenicRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/cryogenic_ruler_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CryogenicRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/cryogenic_ruler_armor.png");
   }

   public ResourceLocation getAnimationResource(CryogenicRulerArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "animations/armor/cryogenic_ruler_armor.animation.json");
   }
}
