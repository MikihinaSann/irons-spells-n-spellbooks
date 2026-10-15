package net.hazen.hazennstuff.Item.Staves.SoulcallingScepter;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class SoulcallingScepterRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/staves/soulcalling_scepter.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/staves/soulcalling_scepter.png");

   public SoulcallingScepterRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).setAnimatorProvider(SoulcallingScepterAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
