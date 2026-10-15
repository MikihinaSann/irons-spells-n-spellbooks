package net.hazen.hazennstuff.Item.Armor.Geckolib.CrystalArachnid;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CrystalArachnidArmorModel extends DefaultedEntityGeoModel<CrystalArachnidArmorItem> {
   public CrystalArachnidArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(CrystalArachnidArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/crystal_arachnid_armor.geo.json");
   }

   public ResourceLocation getTextureResource(CrystalArachnidArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/crystal_arachnid_armor.png");
   }

   public ResourceLocation getAnimationResource(CrystalArachnidArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
