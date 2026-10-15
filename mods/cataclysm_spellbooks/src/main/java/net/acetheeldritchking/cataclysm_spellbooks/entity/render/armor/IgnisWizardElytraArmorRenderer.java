package net.acetheeldritchking.cataclysm_spellbooks.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.acetheeldritchking.cataclysm_spellbooks.items.armor.animators.IgnisWizardElytraAnimator;
import net.minecraft.resources.ResourceLocation;

public class IgnisWizardElytraArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "geo/ignis_armor_winged.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "textures/models/armor/ignis_armor_winged.png");

   public IgnisWizardElytraArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(IgnisWizardElytraAnimator::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .setPipelineContext(AzArmorLeggingTorsoLayerPipeline::new)
            .build()
      );
   }
}
