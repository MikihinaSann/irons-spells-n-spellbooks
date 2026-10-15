package net.alshanex.tunes_n_tomes.event;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.util.SoundMarkerManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.Clone;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;

@EventBusSubscriber(
   modid = "tunes_n_tomes"
)
public class SoundMarkerEvents {
   @SubscribeEvent
   public static void onSoundSpellCast(SpellOnCastEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         if (!player.level().isClientSide) {
            if (event.getSchoolType() == TSchoolRegistry.MELODY.get()) {
               SoundMarkerManager.onSoundSpellCast(player, event.getSpellId(), event.getSpellLevel());
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         MagicData magicData = MagicData.getPlayerMagicData(player);
         if (!magicData.isCasting()) {
            SoundMarkerManager.onPlayerStopCasting(player);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         SoundMarkerManager.cleanupPlayerMarkers(serverPlayer);
      }
   }

   @SubscribeEvent
   public static void onPlayerClone(Clone event) {
      if (event.getEntity() instanceof ServerPlayer newPlayer) {
         SoundMarkerManager.syncMarkerData(newPlayer);
      }
   }
}
