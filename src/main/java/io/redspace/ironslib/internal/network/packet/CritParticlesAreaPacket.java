package io.redspace.ironslib.internal.network.packet;

import io.redspace.ironslib.internal.client.ClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CritParticlesAreaPacket implements CustomPacketPayload {
   public static final Type<CritParticlesAreaPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("irons_lib", "crit_particles_area"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CritParticlesAreaPacket> STREAM_CODEC = CustomPacketPayload.codec(
      CritParticlesAreaPacket::write, CritParticlesAreaPacket::new
   );
   private final BlockPos minPos;
   private final BlockPos maxPos;

   public CritParticlesAreaPacket(BlockPos minPos, BlockPos maxPos) {
      this.minPos = minPos;
      this.maxPos = maxPos;
   }

   public CritParticlesAreaPacket(FriendlyByteBuf buf) {
      this.minPos = buf.readBlockPos();
      this.maxPos = buf.readBlockPos();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeBlockPos(this.minPos);
      buf.writeBlockPos(this.maxPos);
   }

   public static void handle(CritParticlesAreaPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> ClientHelper.spawnCritParticlesInArea(packet.minPos, packet.maxPos));
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
