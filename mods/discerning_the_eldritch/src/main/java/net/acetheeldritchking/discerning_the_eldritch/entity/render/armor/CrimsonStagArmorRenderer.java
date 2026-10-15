package net.acetheeldritchking.discerning_the_eldritch.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class CrimsonStagArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/crimson_stag.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/crimson_stag.png");

   public CrimsonStagArmorRenderer() {
      super(AzArmorRendererConfig.builder(GEO, TEX).setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
