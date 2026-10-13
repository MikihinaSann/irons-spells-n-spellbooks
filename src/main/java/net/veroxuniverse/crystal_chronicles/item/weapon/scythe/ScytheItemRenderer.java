package net.veroxuniverse.crystal_chronicles.item.weapon.scythe;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class ScytheItemRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "geo/blood_scythe.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "textures/item/blood_scythe.png");

   public ScytheItemRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
