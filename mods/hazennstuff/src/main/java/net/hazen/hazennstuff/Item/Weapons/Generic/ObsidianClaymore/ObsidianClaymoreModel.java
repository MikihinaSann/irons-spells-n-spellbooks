package net.hazen.hazennstuff.Item.Weapons.Generic.ObsidianClaymore;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ObsidianClaymoreModel extends DefaultedItemGeoModel<ObsidianClaymoreItem> {
   public ObsidianClaymoreModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(ObsidianClaymoreItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/obsidian_claymore.geo.json");
   }

   public ResourceLocation getTextureResource(ObsidianClaymoreItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/obsidian_claymore.png");
   }

   public ResourceLocation getAnimationResource(ObsidianClaymoreItem animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
