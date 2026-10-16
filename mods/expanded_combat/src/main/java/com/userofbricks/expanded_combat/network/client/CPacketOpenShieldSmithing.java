package com.userofbricks.expanded_combat.network.client;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CPacketOpenShieldSmithing(ItemStack carried, BlockPos accessPos) implements CustomPacketPayload {
   public static final Type<CPacketOpenShieldSmithing> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("expanded_combat", "open_shield_smithing"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CPacketOpenShieldSmithing> STREAM_CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC,
      CPacketOpenShieldSmithing::carried,
      BlockPos.STREAM_CODEC,
      CPacketOpenShieldSmithing::accessPos,
      CPacketOpenShieldSmithing::new
   );

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
