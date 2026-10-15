package net.hazen.hazennstuff.Item.Curios.Wings.ArbitriumWings;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class ArbitriumWingsCurioItemRenderer extends AzArmorRenderer {
   public static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/curios/arbitrium_wings.geo.json");
   public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/armor/arbitrium_robes_armor.png");

   public ArbitriumWingsCurioItemRenderer() {
      super(AzArmorRendererConfig.builder(GEO, TEX).setAnimatorProvider(ArbitriumWingsAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
