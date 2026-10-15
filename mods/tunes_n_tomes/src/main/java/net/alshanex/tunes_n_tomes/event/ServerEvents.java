package net.alshanex.tunes_n_tomes.event;

import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import net.alshanex.tunes_n_tomes.entity.segno_marker.SegnoMarkerEntity;
import net.alshanex.tunes_n_tomes.util.LastSpellTracker;
import net.alshanex.tunes_n_tomes.util.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

@EventBusSubscriber(
   modid = "tunes_n_tomes"
)
public class ServerEvents {
   @SubscribeEvent
   public static void onSpellCasted(SpellOnCastEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
         LastSpellTracker.track(player, event.getSpellId(), event.getSpellLevel());
      }
   }

   @SubscribeEvent
   public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
      if (event.getNewAboutToBeSetTarget() instanceof SegnoMarkerEntity) {
         LivingEntity attacker = event.getEntity();
         if (attacker.getType().is(ModTags.CANNOT_TARGET_MARKERS)) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLogout(PlayerLoggedOutEvent event) {
      if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer serverPlayer) {
         LastSpellTracker.clear(serverPlayer.getUUID());
      }
   }
}
