package com.userofbricks.expanded_combat.network.client;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CPacketOpenSmithing(ItemStack carried, BlockPos accessPos) implements CustomPacketPayload {
   public static final Type<CPacketOpenSmithing> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("expanded_combat", "open_smithing"));
   public static final StreamCodec<RegistryFriendlyByteBuf, CPacketOpenSmithing> STREAM_CODEC = StreamCodec.composite(
      ItemStack.OPTIONAL_STREAM_CODEC, CPacketOpenSmithing::carried, BlockPos.STREAM_CODEC, CPacketOpenSmithing::accessPos, CPacketOpenSmithing::new
   );

   @Nonnull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }
}
