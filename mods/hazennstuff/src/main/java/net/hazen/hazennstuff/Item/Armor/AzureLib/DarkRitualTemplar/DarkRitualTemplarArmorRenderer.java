package net.hazen.hazennstuff.Item.Armor.AzureLib.DarkRitualTemplar;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.AzArmorLeggingTorsoLayerPipeline;
import net.minecraft.resources.ResourceLocation;

public class DarkRitualTemplarArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/dark_ritual_templar_armor.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/dark_ritual_templar_armor.png");

   public DarkRitualTemplarArmorRenderer() {
      super(AzArmorRendererConfig.builder(GEO, TEX).addRenderLayer(new AzAutoGlowingLayer()).setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new).build());
   }
}
