package net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.animators.ExcelsiusPowerAnimator;
import net.minecraft.resources.ResourceLocation;

public class ExcelsiusPowerArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/excelsius_attack.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/models/armor/excelsius_spell_power.png");
   private static final ResourceLocation TEX_CHARGED = ResourceLocation.fromNamespaceAndPath(
      "cataclysm_spellbooks", "textures/models/armor/excelsius_spell_power_overcharged.png"
   );

   public ExcelsiusPowerArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(ExcelsiusPowerAnimator::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new)
            .build()
      );
   }
}
