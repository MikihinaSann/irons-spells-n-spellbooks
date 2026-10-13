package net.veroxuniverse.crystal_chronicles.item.armor.electromancer;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;
import net.veroxuniverse.crystal_chronicles.item.armor.ArmorAnimator;

public class ElectromancerArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "geo/electromancer.geo.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "textures/armor/electromancer.png");

   public ElectromancerArmorRenderer() {
      super(AzArmorRendererConfig.builder(MODEL, TEXTURE).setAnimatorProvider(ArmorAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
