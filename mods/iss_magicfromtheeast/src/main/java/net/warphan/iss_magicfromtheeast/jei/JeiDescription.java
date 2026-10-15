package net.warphan.iss_magicfromtheeast.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.warphan.iss_magicfromtheeast.registries.MFTEItemRegistries;

@JeiPlugin
public class JeiDescription implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return new ResourceLocation("iss_magicfromtheeast", "jei_plugin");
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addItemStackInfo(
         new ItemStack((ItemLike)MFTEItemRegistries.BOTTLE_OF_SOULS.get()),
         new Component[]{Component.translatable("item.iss_magicfromtheeast.bottle_of_souls.guide")}
      );
      registration.addItemStackInfo(
         new ItemStack((ItemLike)MFTEItemRegistries.CRYSTALLIZED_SOUL.get()),
         new Component[]{Component.translatable("item.iss_magicfromtheeast.crystallized_soul.guide")}
      );
   }
}
