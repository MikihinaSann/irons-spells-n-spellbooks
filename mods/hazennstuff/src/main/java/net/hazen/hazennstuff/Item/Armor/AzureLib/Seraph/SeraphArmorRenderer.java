package net.hazen.hazennstuff.Item.Armor.AzureLib.Seraph;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.AzArmorLeggingTorsoLayerPipeline;
import net.minecraft.resources.ResourceLocation;

public class SeraphArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/seraph_armor.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/seraph_armor.png");

   public SeraphArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(SeraphAnimator::new)
            .setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .build()
      );
   }
}
