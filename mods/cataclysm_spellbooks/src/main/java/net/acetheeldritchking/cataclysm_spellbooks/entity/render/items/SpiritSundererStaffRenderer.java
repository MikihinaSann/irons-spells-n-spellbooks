package net.acetheeldritchking.cataclysm_spellbooks.entity.render.items;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class SpiritSundererStaffRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/spirit_sunderer.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/item/spirit_sunderer.png");

   public SpiritSundererStaffRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
