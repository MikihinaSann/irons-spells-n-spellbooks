package net.hazen.hazennstuff.Item.Curios.Sheaths.GalvanizedSheath;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class GalvanizedSheathItemRenderer extends AzItemRenderer {
   public static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/curios/galvanized_sheath.geo.json");
   public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/curios/galvanized_sheath.png");

   public GalvanizedSheathItemRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).setAnimatorProvider(GalvanizedSheathAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
