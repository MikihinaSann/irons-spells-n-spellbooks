package net.hazen.hazennstuff.Item.Weapons.Generic.LegionnaireWarlockAxe;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class LegionnaireWarlockAxeModel extends DefaultedItemGeoModel<LegionnaireWarlockAxeItem> {
   public LegionnaireWarlockAxeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(LegionnaireWarlockAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/legionnaire_warlock_axe.geo.json");
   }

   public ResourceLocation getTextureResource(LegionnaireWarlockAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/legionnaire_warlock_axe.png");
   }

   public ResourceLocation getAnimationResource(LegionnaireWarlockAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
