package com.somake.somakespells.network;

import com.somake.somakespells.spells.fire.FireOrbsSpell;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FireOrbsAttackPacket() implements CustomPacketPayload {
   public static final Type<FireOrbsAttackPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("somakespells", "fire_orbs_attack"));
   public static final StreamCodec<RegistryFriendlyByteBuf, FireOrbsAttackPacket> STREAM_CODEC = CustomPacketPayload.codec(
      FireOrbsAttackPacket::write, FireOrbsAttackPacket::new
   );

   public FireOrbsAttackPacket(RegistryFriendlyByteBuf buffer) {
      this();
   }

   private void write(RegistryFriendlyByteBuf buffer) {
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(FireOrbsAttackPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() instanceof ServerPlayer serverPlayer) {
            FireOrbsSpell.handleBasicAttack(serverPlayer);
         }
      });
   }
}
