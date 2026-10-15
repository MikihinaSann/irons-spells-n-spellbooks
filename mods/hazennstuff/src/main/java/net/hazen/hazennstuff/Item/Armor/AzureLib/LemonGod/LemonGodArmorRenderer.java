package net.hazen.hazennstuff.Item.Armor.AzureLib.LemonGod;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.hazen.hazennstuff.Item.Armor.AzureLib.AzArmorLeggingTorsoLayerPipeline;
import net.minecraft.resources.ResourceLocation;

public class LemonGodArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/armor/lemon_god_armor.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/lemon_god_armor_azurelib.png");

   public LemonGodArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(LemonGodAnimator::new)
            .setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .build()
      );
   }
}
