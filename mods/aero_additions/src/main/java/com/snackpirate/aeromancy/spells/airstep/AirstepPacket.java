package com.snackpirate.aeromancy.spells.airstep;

import com.snackpirate.aeromancy.Aeromancy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class AirstepPacket implements CustomPacketPayload {
   public static final Type<AirstepPacket> TYPE = new Type(Aeromancy.id("airstep"));
   public static final StreamCodec<RegistryFriendlyByteBuf, AirstepPacket> STREAM_CODEC = CustomPacketPayload.codec(AirstepPacket::write, AirstepPacket::new);
   public final int deltaX;
   public final int deltaZ;

   public AirstepPacket(FriendlyByteBuf buf) {
      this.deltaX = buf.readInt();
      this.deltaZ = buf.readInt();
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public AirstepPacket(int x, int z) {
      this.deltaX = x;
      this.deltaZ = z;
   }

   private void write(FriendlyByteBuf buf) {
      buf.writeInt(this.deltaX);
      buf.writeInt(this.deltaZ);
   }

   public static void handle(AirstepPacket packet, IPayloadContext ctx) {
      ctx.enqueueWork(() -> AirstepSpell.airstepJump(packet, ctx.player()));
   }
}
