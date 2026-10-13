package io.redspace.ironslib.attribute.data_handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.redspace.ironslib.IronsLib;
import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.redspace.ironslib.attribute.network.SyncAttributeRemapsPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class AttributeRemapReloadListener extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new GsonBuilder().create();

   public AttributeRemapReloadListener() {
      super(GSON, "irons_lib_attribute_remap");
   }

   protected void apply(Map<ResourceLocation, JsonElement> resources, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
      List<AttributeRemapEntry> remaps = new ArrayList<>();
      resources.forEach(
         (id, json) -> AttributeRemapEntry.CODEC
               .parse(JsonOps.INSTANCE, json)
               .resultOrPartial(err -> IronsLib.LOGGER.error("Failed to parse attribute remap {}: {}", id, err))
               .ifPresent(remaps::add)
      );
      AttributeRemapRegistry.handleDatapackSync(remaps);
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      if (server != null) {
         SyncAttributeRemapsPacket packet = AttributeRemapRegistry.buildSyncPacket();
         PacketDistributor.sendToAllPlayers(packet, new CustomPacketPayload[0]);
      }
   }
}
