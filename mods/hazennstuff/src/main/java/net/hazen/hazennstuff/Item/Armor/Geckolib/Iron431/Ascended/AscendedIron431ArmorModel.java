package net.hazen.hazennstuff.Item.Armor.Geckolib.Iron431.Ascended;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AscendedIron431ArmorModel extends DefaultedEntityGeoModel<AscendedIron431ArmorItem> {
   public AscendedIron431ArmorModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(AscendedIron431ArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/irons_armor.geo.json");
   }

   public ResourceLocation getTextureResource(AscendedIron431ArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/irons_armor.png");
   }

   public ResourceLocation getAnimationResource(AscendedIron431ArmorItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
