package net.veroxuniverse.crystal_chronicles.item.weapon.chakram;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class ChakramItemRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "geo/chakram.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "textures/item/chakram.png");

   public ChakramItemRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
