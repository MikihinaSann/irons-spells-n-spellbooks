package net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;

public class MonstrousWizardHatArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/monstrous_wizard_hat.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/models/armor/monstrous_wizard_hat.png");

   public MonstrousWizardHatArmorRenderer() {
      super(AzArmorRendererConfig.builder(GEO, TEX).setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new).build());
   }
}
