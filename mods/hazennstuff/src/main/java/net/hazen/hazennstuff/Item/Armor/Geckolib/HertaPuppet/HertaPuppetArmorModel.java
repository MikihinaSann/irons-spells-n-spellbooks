package net.hazen.hazennstuff.Item.Armor.Geckolib.HertaPuppet;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HertaPuppetArmorModel extends DefaultedEntityGeoModel<HertaPuppetArmorItem> {
   public HertaPuppetArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(HertaPuppetArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/herta_puppet_armor.geo.json");
   }

   public ResourceLocation getTextureResource(HertaPuppetArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/herta_puppet_armor.png");
   }

   public ResourceLocation getAnimationResource(HertaPuppetArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
