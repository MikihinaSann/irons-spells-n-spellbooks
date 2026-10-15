package net.acetheeldritchking.aces_spell_utils.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class AddShaderEffectPacket implements CustomPacketPayload {
   public static final Type<AddShaderEffectPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "add_shader_effect"));
   public static final StreamCodec<RegistryFriendlyByteBuf, AddShaderEffectPacket> STREAM_CODEC = CustomPacketPayload.codec(
      AddShaderEffectPacket::write, AddShaderEffectPacket::new
   );
   private final String modid;
   private final String location;

   public AddShaderEffectPacket(String modid, String location) {
      this.modid = modid;
      this.location = location;
   }

   public AddShaderEffectPacket(FriendlyByteBuf buf) {
      this.modid = buf.readUtf();
      this.location = buf.readUtf();
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeUtf(this.modid);
      buf.writeUtf(this.location);
   }

   public static void handle(AddShaderEffectPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> PayloadHandler.handleClientBoundShaderEffect(packet.modid, packet.location));
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
