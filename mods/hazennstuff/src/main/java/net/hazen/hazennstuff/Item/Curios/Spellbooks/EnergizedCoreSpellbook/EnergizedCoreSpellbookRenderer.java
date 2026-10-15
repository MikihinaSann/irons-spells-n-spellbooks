package net.hazen.hazennstuff.Item.Curios.Spellbooks.EnergizedCoreSpellbook;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class EnergizedCoreSpellbookRenderer extends AzItemRenderer {
   public static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/curios/energized_core_spellbook.geo.json");
   public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/curios/energized_core_spellbook.png");

   public EnergizedCoreSpellbookRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).setAnimatorProvider(EnergizedCoreSpellbookAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
