package net.hazen.hazennstuff.Item.Curios.Sheaths.ScrollSheath;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;

public class ScrollSheathItemRenderer extends AzItemRenderer {
   public static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("hazennstuff", "geo/curios/scroll_sheath.geo.json");
   public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("hazennstuff", "textures/curios/scroll_sheath.png");

   public ScrollSheathItemRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).setAnimatorProvider(ScrollSheathAnimator::new).build());
   }
}
