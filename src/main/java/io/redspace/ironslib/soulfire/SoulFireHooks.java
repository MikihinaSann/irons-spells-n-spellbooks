package io.redspace.ironslib.soulfire;

import io.redspace.ironslib.soulfire.network.ClientboundSoulFirePacket;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.StartTracking;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "irons_lib"
)
public final class SoulFireHooks {
   private static final Set<Entity> ENTITIES_TO_SYNC = new HashSet<>();

   private SoulFireHooks() {
   }

   static SoulFireState state(Entity entity) {
      return ((ISoulFireEntity)entity).irons_lib$getSoulFireState();
   }

   public static void onSetRemainingFireTicks(Entity entity, int oldTicks, int newTicks) {
      boolean freshBurn = oldTicks <= 0 && newTicks > 0;
      if (freshBurn) {
         SoulFireState state = state(entity);
         if (entity.level().isClientSide()) {
            state.localSoul = ((ISoulFireEntity)entity).irons_lib$isOnFireShared() && state.soul;
         } else if (state.soulRequestedAt != entity.level().getGameTime()) {
            setSoul(entity, state, false);
         }
      }
   }

   public static void onInsideSoulFire(Entity entity) {
      if (entity.level().isClientSide()) {
         state(entity).localSoul = true;
      } else {
         SoulFireHelper.setSoulFire(entity, true);
      }
   }

   static void setSoul(Entity entity, SoulFireState state, boolean soul) {
      state.soul = soul;
      if (soul != state.sentSoul) {
         ENTITIES_TO_SYNC.add(entity);
      }
   }

   public static void onSoulFirePacket(Entity entity, boolean soul) {
      state(entity).soul = soul;
   }

   @SubscribeEvent
   public static void flush(Post event) {
      for (Entity entity : ENTITIES_TO_SYNC) {
         SoulFireState state = state(entity);
         if (!entity.isRemoved() && state.soul != state.sentSoul) {
            state.sentSoul = state.soul;
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, new ClientboundSoulFirePacket(entity.getId(), state.soul), new CustomPacketPayload[0]);
         }
      }

      ENTITIES_TO_SYNC.clear();
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      Entity target = event.getTarget();
      if (event.getEntity() instanceof ServerPlayer player && state(target).soul) {
         PacketDistributor.sendToPlayer(player, new ClientboundSoulFirePacket(target.getId(), true), new CustomPacketPayload[0]);
      }
   }

   @SubscribeEvent
   public static void onChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player && state(player).soul) {
         PacketDistributor.sendToPlayer(player, new ClientboundSoulFirePacket(player.getId(), true), new CustomPacketPayload[0]);
      }
   }
}
