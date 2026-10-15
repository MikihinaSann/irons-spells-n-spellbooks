package net.hazen.hazennstuff.Item.Staves.InsaniaAeternus;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class InsaniaAeternusRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/insania_aeternus.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/insania_aeternus.png");

   public InsaniaAeternusRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).setAnimatorProvider(InsaniaAeternusAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
