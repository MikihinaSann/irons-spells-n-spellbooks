package net.hazen.hazennstuff.Item.Armor.Geckolib.NamelessOneArmor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class NamelessOneArmorModel extends DefaultedEntityGeoModel<NamelessOneArmorItem> {
   public NamelessOneArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(NamelessOneArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/skeleton_robes.geo.json");
   }

   public ResourceLocation getTextureResource(NamelessOneArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/the_nameless_one.png");
   }

   public ResourceLocation getAnimationResource(NamelessOneArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
