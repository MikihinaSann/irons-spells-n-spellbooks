package net.acetheeldritchking.discerning_the_eldritch.entity.render.items;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

public class CastersMantleCurioItemRenderer extends AzArmorRenderer {
   public static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/casters_mantle.geo.json");
   public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/casters_mantle_armor.png");

   public CastersMantleCurioItemRenderer() {
      super(AzArmorRendererConfig.builder(GEO, TEX).build());
   }
}
