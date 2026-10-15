package net.hazen.hazennstuff.Item.Util.PickaxeAxe;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class PickaxeAxeModel extends DefaultedItemGeoModel<PickaxeAxeItem> {
   public PickaxeAxeModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(PickaxeAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/tools/pickaxe_axe.geo.json");
   }

   public ResourceLocation getTextureResource(PickaxeAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/tools/pickaxe_axe.png");
   }

   public ResourceLocation getAnimationResource(PickaxeAxeItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
