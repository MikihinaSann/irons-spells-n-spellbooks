package net.veroxuniverse.crystal_chronicles.item.weapon.staff;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;

public class StaffItemRenderer extends AzItemRenderer {
   private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "geo/staff.geo.json");
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "textures/item/staff.png");

   public StaffItemRenderer() {
      super(AzItemRendererConfig.builder(GEO, TEX).addRenderLayer(new AzAutoGlowingLayer()).setAnimatorProvider(StaffItemAnimation::new).build());
   }
}
