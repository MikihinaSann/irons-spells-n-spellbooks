package net.hazen.hazennstuff.Item.Weapons.Generic.HammerOfJustice;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class HammerOfJusticeRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/item/weapons/hammer_of_justice.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/item/weapons/hammer_of_justice.png");

   public HammerOfJusticeRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).setAnimatorProvider(HammerOfJusticeAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
