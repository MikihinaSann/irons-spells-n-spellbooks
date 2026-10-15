package net.hazen.hazennstuff.Item.Staves.RodOfDiscord;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RodOfDiscordModel extends DefaultedItemGeoModel<RodOfDiscord> {
   public RodOfDiscordModel() {
      super(ResourceLocation.fromNamespaceAndPath("hazennstuff", ""));
   }

   public ResourceLocation getModelResource(RodOfDiscord animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/rod_of_discord.geo.json");
   }

   public ResourceLocation getTextureResource(RodOfDiscord animatable) {
      return ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/rod_of_discord.png");
   }

   public ResourceLocation getAnimationResource(RodOfDiscord animatable) {
      return ResourceLocation.fromNamespaceAndPath("irons_spellbooks", "animations/wizard_armor_animation.json");
   }
}
