package net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class BloomStoneMageArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/bloom_stone_mage.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/models/armor/bloom_stone_mage.png");

   public BloomStoneMageArmorRenderer() {
      super(AzArmorRendererConfig.builder(GEO, TEX).addRenderLayer(new AzAutoGlowingLayer()).setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new).build());
   }
}
