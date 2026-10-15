package net.hazen.hazennstuff.Item.Weapons.Generic.WarFlamingLance;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarFlamingLanceModel extends DefaultedItemGeoModel<WarFlamingLanceItem> {
   public WarFlamingLanceModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(WarFlamingLanceItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/war_flaming_lance.geo.json");
   }

   public ResourceLocation getTextureResource(WarFlamingLanceItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/war_flaming_lance.png");
   }

   public ResourceLocation getAnimationResource(WarFlamingLanceItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
