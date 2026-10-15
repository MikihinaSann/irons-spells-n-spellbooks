package com.snackpirate.aeromancy.item;

import com.snackpirate.aeromancy.Aeromancy;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class JeiCompat implements IModPlugin {
   public ResourceLocation getPluginUid() {
      return Aeromancy.id("jei_compat");
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addItemStackInfo(
         new ItemStack((ItemLike)AAItems.UPDRAFT_TOME.get()), new Component[]{Component.translatable("item.aero_additions.updraft_tome.guide")}
      );
   }
}
