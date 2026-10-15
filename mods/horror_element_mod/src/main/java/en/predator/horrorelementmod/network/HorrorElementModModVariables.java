package en.predator.horrorelementmod.network;

import en.predator.horrorelementmod.HorrorElementModMod;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class HorrorElementModModVariables {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(Keys.ATTACHMENT_TYPES, "horror_element_mod");
   public static double soundtoplay = 0.0;
   public static double mobsspawn = 0.0;

   @SubscribeEvent
   public static void init(FMLCommonSetupEvent event) {
      HorrorElementModMod.addNetworkMessage(
         HorrorElementModModVariables.SavedDataSyncMessage.TYPE,
         HorrorElementModModVariables.SavedDataSyncMessage.STREAM_CODEC,
         HorrorElementModModVariables.SavedDataSyncMessage::handleData
      );
   }

   @EventBusSubscriber
   public static class EventBusVariableHandlers {
      @SubscribeEvent
      public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            SavedData mapdata = HorrorElementModModVariables.MapVariables.get(event.getEntity().level());
            SavedData worlddata = HorrorElementModModVariables.WorldVariables.get(event.getEntity().level());
            if (mapdata != null) {
               PacketDistributor.sendToPlayer(player, new HorrorElementModModVariables.SavedDataSyncMessage(0, mapdata), new CustomPacketPayload[0]);
            }

            if (worlddata != null) {
               PacketDistributor.sendToPlayer(player, new HorrorElementModModVariables.SavedDataSyncMessage(1, worlddata), new CustomPacketPayload[0]);
            }
         }
      }

      @SubscribeEvent
      public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
         if (event.getEntity() instanceof ServerPlayer player) {
            SavedData worlddata = HorrorElementModModVariables.WorldVariables.get(event.getEntity().level());
            if (worlddata != null) {
               PacketDistributor.sendToPlayer(player, new HorrorElementModModVariables.SavedDataSyncMessage(1, worlddata), new CustomPacketPayload[0]);
            }
         }
      }
   }

   public static class MapVariables extends SavedData {
      public static final String DATA_NAME = "horror_element_mod_mapvars";
      public boolean atmosphere = false;
      public boolean SCREAMER = false;
      public boolean DayReset = true;
      static HorrorElementModModVariables.MapVariables clientSide = new HorrorElementModModVariables.MapVariables();

      public static HorrorElementModModVariables.MapVariables load(CompoundTag tag, Provider lookupProvider) {
         HorrorElementModModVariables.MapVariables data = new HorrorElementModModVariables.MapVariables();
         data.read(tag, lookupProvider);
         return data;
      }

      public void read(CompoundTag nbt, Provider lookupProvider) {
         this.atmosphere = nbt.getBoolean("atmosphere");
         this.SCREAMER = nbt.getBoolean("SCREAMER");
         this.DayReset = nbt.getBoolean("DayReset");
      }

      public CompoundTag save(CompoundTag nbt, Provider lookupProvider) {
         nbt.putBoolean("atmosphere", this.atmosphere);
         nbt.putBoolean("SCREAMER", this.SCREAMER);
         nbt.putBoolean("DayReset", this.DayReset);
         return nbt;
      }

      public void syncData(LevelAccessor world) {
         this.setDirty();
         if (world instanceof Level && !world.isClientSide()) {
            PacketDistributor.sendToAllPlayers(new HorrorElementModModVariables.SavedDataSyncMessage(0, this), new CustomPacketPayload[0]);
         }
      }

      public static HorrorElementModModVariables.MapVariables get(LevelAccessor world) {
         return world instanceof ServerLevelAccessor serverLevelAcc
            ? (HorrorElementModModVariables.MapVariables)serverLevelAcc.getLevel()
               .getServer()
               .getLevel(Level.OVERWORLD)
               .getDataStorage()
               .computeIfAbsent(
                  new Factory<HorrorElementModModVariables.MapVariables>(HorrorElementModModVariables.MapVariables::new, HorrorElementModModVariables.MapVariables::load, null), "horror_element_mod_mapvars"
               )
            : clientSide;
      }
   }

   public record SavedDataSyncMessage(int dataType, SavedData data) implements CustomPacketPayload {
      public static final Type<HorrorElementModModVariables.SavedDataSyncMessage> TYPE = new Type(
         ResourceLocation.fromNamespaceAndPath("horror_element_mod", "saved_data_sync")
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, HorrorElementModModVariables.SavedDataSyncMessage> STREAM_CODEC = StreamCodec.of(
         (buffer, message) -> {
            buffer.writeInt(message.dataType);
            if (message.data != null) {
               buffer.writeNbt(message.data.save(new CompoundTag(), buffer.registryAccess()));
            }
         }, buffer -> {
            int dataType = buffer.readInt();
            CompoundTag nbt = buffer.readNbt();
            SavedData data = null;
            if (nbt != null) {
               data = (SavedData)(dataType == 0 ? new HorrorElementModModVariables.MapVariables() : new HorrorElementModModVariables.WorldVariables());
               if (data instanceof HorrorElementModModVariables.MapVariables mapVariables) {
                  mapVariables.read(nbt, buffer.registryAccess());
               } else if (data instanceof HorrorElementModModVariables.WorldVariables worldVariables) {
                  worldVariables.read(nbt, buffer.registryAccess());
               }
            }

            return new HorrorElementModModVariables.SavedDataSyncMessage(dataType, data);
         }
      );

      public Type<HorrorElementModModVariables.SavedDataSyncMessage> type() {
         return TYPE;
      }

      public static void handleData(HorrorElementModModVariables.SavedDataSyncMessage message, IPayloadContext context) {
         if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
            context.enqueueWork(
                  () -> {
                     if (message.dataType == 0) {
                        HorrorElementModModVariables.MapVariables.clientSide
                           .read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
                     } else {
                        HorrorElementModModVariables.WorldVariables.clientSide
                           .read(message.data.save(new CompoundTag(), context.player().registryAccess()), context.player().registryAccess());
                     }
                  }
               )
               .exceptionally(e -> {
                  context.connection().disconnect(Component.literal(e.getMessage()));
                  return null;
               });
         }
      }
   }

   public static class WorldVariables extends SavedData {
      public static final String DATA_NAME = "horror_element_mod_worldvars";
      static HorrorElementModModVariables.WorldVariables clientSide = new HorrorElementModModVariables.WorldVariables();

      public static HorrorElementModModVariables.WorldVariables load(CompoundTag tag, Provider lookupProvider) {
         HorrorElementModModVariables.WorldVariables data = new HorrorElementModModVariables.WorldVariables();
         data.read(tag, lookupProvider);
         return data;
      }

      public void read(CompoundTag nbt, Provider lookupProvider) {
      }

      public CompoundTag save(CompoundTag nbt, Provider lookupProvider) {
         return nbt;
      }

      public void syncData(LevelAccessor world) {
         this.setDirty();
         if (world instanceof ServerLevel level) {
            PacketDistributor.sendToPlayersInDimension(level, new HorrorElementModModVariables.SavedDataSyncMessage(1, this));
         }
      }

      public static HorrorElementModModVariables.WorldVariables get(LevelAccessor world) {
         return world instanceof ServerLevel level
            ? (HorrorElementModModVariables.WorldVariables)level.getDataStorage()
               .computeIfAbsent(
                  new Factory<HorrorElementModModVariables.WorldVariables>(HorrorElementModModVariables.WorldVariables::new, HorrorElementModModVariables.WorldVariables::load, null),
                  "horror_element_mod_worldvars"
               )
            : clientSide;
      }
   }
}
