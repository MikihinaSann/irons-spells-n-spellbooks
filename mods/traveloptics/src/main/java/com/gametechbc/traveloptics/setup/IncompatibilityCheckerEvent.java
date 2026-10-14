package com.gametechbc.traveloptics.setup;

import com.gametechbc.traveloptics.config.CommonConfig;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber(
   modid = "traveloptics"
)
public class IncompatibilityCheckerEvent {
   private static final String BASE_TAG = "FirstLogin_TOMod_";
   private static final Map<String, String> INCOMPATIBLE_MODS = Map.of(
      "epicfight",
      "Epic Fight",
      "optifine",
      "OptiFine",
      "armorunlocked",
      "Armor Unlocked",
      "canary",
      "Canary",
      "doespotatotick",
      "Does Potato Tick",
      "doesittick",
      "Does It Tick"
   );

   public IncompatibilityCheckerEvent() {
      super();
   }

   @SubscribeEvent
   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && (Boolean)CommonConfig.showWelcomeMessage.get()) {
         CompoundTag playerData = player.getPersistentData();
         CompoundTag persistentTag = playerData.getCompound("PlayerPersisted");
         List<String> foundMods = ModList.get()
            .getMods()
            .stream()
            .map(mod -> mod.getModId().toLowerCase())
            .filter(INCOMPATIBLE_MODS::containsKey)
            .filter(modId -> !persistentTag.getBoolean("FirstLogin_TOMod_" + modId))
            .toList();
         if (!foundMods.isEmpty()) {
            String modList = foundMods.stream().map(INCOMPATIBLE_MODS::get).collect(Collectors.joining(", "));
            String warningMessage = "§cT.O Magic 'n Extras: Thanks for Downloading! We have found "
               + modList
               + (
                  foundMods.size() == 1
                     ? " mod in your list. We are not fully compatible with this mod, check faq-from-dev channel on our discord for detailed information."
                     : " mods in your list. We are not fully compatible with these mods, check faq-from-dev channel on our discord for detailed information."
               );
            player.sendSystemMessage(Component.literal(warningMessage));
            foundMods.forEach(modId -> persistentTag.putBoolean("FirstLogin_TOMod_" + modId, true));
         } else if (!persistentTag.getBoolean("FirstLogin_TOMod_welcome")) {
            player.sendSystemMessage(
               Component.literal(
                  "§eThank you for downloading T.O Magic 'n Extras! 1.21 port of this mod is still in-dev and all the items might not be obtainable in survival. Current ports are for testing purposes only but the mod is very stable and should not cause crashes! §cDo not report for any survival unobtainable items"
               )
            );
            persistentTag.putBoolean("FirstLogin_TOMod_welcome", true);
         }

         playerData.put("PlayerPersisted", persistentTag);
      }
   }
}
