package com.snackpirate.aeromancy.spells.dash;

import com.snackpirate.aeromancy.Aeromancy;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DashParticlesPacket implements CustomPacketPayload {
   private final Vec3 pos1;
   private final Vec3 pos2;
   public static final Type<DashParticlesPacket> TYPE = new Type(Aeromancy.id("dash_particles"));
   public static final StreamCodec<RegistryFriendlyByteBuf, DashParticlesPacket> STREAM_CODEC = CustomPacketPayload.codec(
      DashParticlesPacket::write, DashParticlesPacket::new
   );

   public DashParticlesPacket(Vec3 pos1, Vec3 pos2) {
      this.pos1 = pos1;
      this.pos2 = pos2;
   }

   public DashParticlesPacket(FriendlyByteBuf buf) {
      this.pos1 = buf.readVec3();
      this.pos2 = buf.readVec3();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeVec3(this.pos1);
      buf.writeVec3(this.pos2);
   }

   public static void handle(DashParticlesPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         LocalPlayer player = Minecraft.getInstance().player;
         Vec3 pos1 = packet.pos1;
         Vec3 pos2 = packet.pos2.scale(0.2);
         if (player != null) {
            ClientLevel level = Minecraft.getInstance().player.clientLevel;
            double width = 0.5;
            float height = 1.0F;

            for (int i = 0; i < 20; i++) {
               double x = pos1.x + Utils.random.nextDouble() * width * 2.0 - width;
               double y = pos1.y + Utils.random.nextDouble() * height * 1.2 * 2.0 - height * 1.2;
               double z = pos1.z + Utils.random.nextDouble() * width * 2.0 - width;
               double dx = pos2.x + Utils.random.nextDouble() * 0.1;
               double dy = pos2.y + Utils.random.nextDouble() * 0.1;
               double dz = pos2.z + Utils.random.nextDouble() * 0.1;
               level.addParticle(ParticleTypes.SPIT, true, x, y, z, -dx, -dy, -dz);
            }
         }
      });
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
