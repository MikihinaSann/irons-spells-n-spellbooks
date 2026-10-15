package net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.animators.ExcelsiusResistAnimator;
import net.minecraft.resources.ResourceLocation;

public class ExcelsiusResistArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/excelsius_defense.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/models/armor/excelsius_spell_resist.png");

   public ExcelsiusResistArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(ExcelsiusResistAnimator::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new)
            .build()
      );
   }
}
