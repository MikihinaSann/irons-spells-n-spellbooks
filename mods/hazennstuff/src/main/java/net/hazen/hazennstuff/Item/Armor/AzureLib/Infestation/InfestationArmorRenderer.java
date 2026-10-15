package net.hazen.hazennstuff.Item.Armor.AzureLib.Infestation;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.AzArmorLeggingTorsoLayerPipeline;
import net.minecraft.resources.ResourceLocation;

public class InfestationArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/infestation_armor.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/infestation_armor_azurelib.png");

   public InfestationArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(InfestationAnimator::new)
            .setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .build()
      );
   }
}
