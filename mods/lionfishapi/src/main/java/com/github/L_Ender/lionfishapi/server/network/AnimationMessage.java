package com.github.L_Ender.lionfishapi.server.network;

import com.github.L_Ender.lionfishapi.LionfishAPI;
import com.github.L_Ender.lionfishapi.server.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AnimationMessage(int entityID, int index) implements CustomPacketPayload {
   public static final Type<AnimationMessage> TYPE = new Type(LionfishAPI.prefix("animation_message"));
   public static final StreamCodec<RegistryFriendlyByteBuf, AnimationMessage> STREAM_CODEC = CustomPacketPayload.codec(
      AnimationMessage::write, AnimationMessage::new
   );

   public AnimationMessage(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readInt());
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeInt(this.entityID());
      buf.writeInt(this.index());
   }

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(AnimationMessage message, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         IAnimatedEntity entity = (IAnimatedEntity)Minecraft.getInstance().level.getEntity(message.entityID());
         if (entity != null) {
            if (message.index == -1) {
               entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
            } else {
               entity.setAnimation(entity.getAnimations()[message.index]);
            }

            entity.setAnimationTick(0);
         }
      });
   }
}
