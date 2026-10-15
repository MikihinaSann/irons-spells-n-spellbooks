package com.legacy.dash.network.c_to_s;

import com.legacy.dash.DashConfig;
import com.legacy.dash.DashMod;
import com.legacy.dash.DashRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DashVisualsPayload() implements CustomPacketPayload {
   public static final Type<DashVisualsPayload> TYPE = new Type(DashMod.locate("send_effects"));
   public static final StreamCodec<RegistryFriendlyByteBuf, DashVisualsPayload> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, DashVisualsPayload>() {
      public void encode(RegistryFriendlyByteBuf buff, DashVisualsPayload packet) {
      }

      public DashVisualsPayload decode(RegistryFriendlyByteBuf buff) {
         return new DashVisualsPayload();
      }
   };

   public Type<DashVisualsPayload> type() {
      return TYPE;
   }

   public static void handler(DashVisualsPayload packet, IPayloadContext context) {
      context.enqueueWork(() -> handlePacket(packet, context));
   }

   private static void handlePacket(DashVisualsPayload packet, IPayloadContext context) {
      if (context.player() instanceof ServerPlayer player) {
         if (player != null && player.level() != null) {
            ServerLevel lvl = (ServerLevel)player.level();
            float width = player.getBbWidth() / 2.0F;
            float height = player.getBbHeight() * 0.3F;
            lvl.sendParticles(ParticleTypes.POOF, player.getX(), player.getY() + height, player.getZ(), 3, width, height, width, 0.08F);
            lvl.sendParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + height, player.getZ(), 1, width, height, width, 0.2F);
            lvl.playSound(null, player.getX(), player.getY(), player.getZ(), DashRegistry.DASH_SOUND.get(), player.getSoundSource(), 1.0F, 2.0F);
            if (DashConfig.useDurability()) {
               player.getItemBySlot(EquipmentSlot.FEET).hurtAndBreak(1, player, EquipmentSlot.FEET);
            }
         }
      }
   }
}
