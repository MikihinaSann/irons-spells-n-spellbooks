package net.hazen.hazennstuff.Item.Util.PickaxeAxe.DivineGold;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class PickaxeAxeDivineGoldModel extends DefaultedItemGeoModel<PickaxeAxeDivineGoldItem> {
   public PickaxeAxeDivineGoldModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(PickaxeAxeDivineGoldItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/tools/pickaxe_axe.geo.json");
   }

   public ResourceLocation getTextureResource(PickaxeAxeDivineGoldItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/tools/pickaxe_axe_divine_gold.png");
   }

   public ResourceLocation getAnimationResource(PickaxeAxeDivineGoldItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
