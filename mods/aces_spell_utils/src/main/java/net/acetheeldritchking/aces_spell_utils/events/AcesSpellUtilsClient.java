package net.acetheeldritchking.aces_spell_utils.events;

import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import java.util.List;
import net.acetheeldritchking.aces_spell_utils.items.weapons.MagicGunItem;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.BossMusicManager;
import net.acetheeldritchking.aces_spell_utils.utils.boss_music.UniqueBossMusicManager;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(
   value = "aces_spell_utils",
   dist = {Dist.CLIENT}
)
@EventBusSubscriber(
   modid = "aces_spell_utils",
   value = {Dist.CLIENT}
)
public class AcesSpellUtilsClient {
   public AcesSpellUtilsClient(ModContainer container) {
   }

   @SubscribeEvent
   static void onClientSetup(FMLClientSetupEvent event) {
   }

   @SubscribeEvent
   public static void onPlayerLogout(LoggingOut event) {
      BossMusicManager.hardStop();
      UniqueBossMusicManager.hardStop();
   }

   @SubscribeEvent
   public static void itemTooltipsEvents(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      MinecraftInstanceHelper.ifPlayerPresent(player -> {
         LocalPlayer localPlayer = (LocalPlayer)player;
         List<Component> lines = event.getToolTip();
         boolean advanced = event.getFlags().isAdvanced();
         if (stack.getItem() instanceof MagicGunItem) {
            ASUtils.handleCastingImplementTooltip(stack, localPlayer, lines, advanced);
         }
      });
   }
}
