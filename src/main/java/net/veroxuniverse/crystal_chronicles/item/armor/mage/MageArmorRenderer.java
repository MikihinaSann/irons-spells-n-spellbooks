package net.veroxuniverse.crystal_chronicles.item.armor.mage;

import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;
import net.veroxuniverse.crystal_chronicles.item.armor.ArmorAnimator;

public class MageArmorRenderer extends AzArmorRenderer {
   private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "geo/mage.geo.json");
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "textures/armor/mage.png");

   public MageArmorRenderer() {
      super(AzArmorRendererConfig.builder(MODEL, TEXTURE).setAnimatorProvider(ArmorAnimator::new).addRenderLayer(new AzAutoGlowingLayer()).build());
   }
}
