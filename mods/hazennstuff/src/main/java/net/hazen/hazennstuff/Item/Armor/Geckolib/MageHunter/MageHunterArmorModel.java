package net.hazen.hazennstuff.Item.Armor.Geckolib.MageHunter;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MageHunterArmorModel extends DefaultedEntityGeoModel<MageHunterArmorItem> {
   public MageHunterArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(MageHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/magehunter_armor.geo.json");
   }

   public ResourceLocation getTextureResource(MageHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/magehunter_armor.png");
   }

   public ResourceLocation getAnimationResource(MageHunterArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
