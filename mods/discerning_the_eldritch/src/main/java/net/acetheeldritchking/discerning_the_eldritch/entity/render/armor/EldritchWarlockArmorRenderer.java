package net.acetheeldritchking.discerning_the_eldritch.entity.render.armor;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.acetheeldritchking.discerning_the_eldritch.items.armor.animators.EldritchArmorAnimator;
import net.minecraft.resources.ResourceLocation;

public class EldritchWarlockArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "geo/eldritch_armor.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "textures/models/armor/eldritch_mage_armor.png");

   public EldritchWarlockArmorRenderer() {
      super(
         AzArmorRendererConfig.builder(GEO, TEX)
            .setAnimatorProvider(EldritchArmorAnimator::new)
            .addRenderLayer(new AzAutoGlowingLayer())
            .setPipelineContext(AzArmorLowerCloakLayerPipeline::new)
            .build()
      );
   }
}
