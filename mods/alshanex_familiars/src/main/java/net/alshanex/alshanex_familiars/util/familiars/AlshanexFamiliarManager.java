package net.alshanex.alshanex_familiars.util.familiars;

import net.alshanex.alshanex_familiars.block.entity.RitualAutomatorBlockEntity;
import net.alshanex.alshanex_familiars.screen.RitualAutomatorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AlshanexFamiliarManager {
   public static void onAutomationScreenSelection(Player localPlayer, BlockPos pos, ResourceLocation recipeId) {
      if (localPlayer instanceof ServerPlayer player && player.level().getBlockEntity(pos) instanceof RitualAutomatorBlockEntity automator) {
         automator.setRecipe(recipeId);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static void openRitualAutomatorScreen(BlockPos pos) {
      Minecraft.getInstance().setScreen(new RitualAutomatorScreen(pos));
   }
}
