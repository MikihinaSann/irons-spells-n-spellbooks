package com.userofbricks.expanded_combat.network;

import com.userofbricks.expanded_combat.inventory.container.ShieldSmithingMenuProvider;
import com.userofbricks.expanded_combat.inventory.container.SmithingMenuProvider;
import com.userofbricks.expanded_combat.network.client.CPacketOpenShieldSmithing;
import com.userofbricks.expanded_combat.network.client.CPacketOpenSmithing;
import com.userofbricks.expanded_combat.network.server.PacketIntAttachment;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class ECServerPayloadHandler {
   private static final ECServerPayloadHandler INSTANCE = new ECServerPayloadHandler();

   public static ECServerPayloadHandler getInstance() {
      return INSTANCE;
   }

   public void handleOpenSmithing(CPacketOpenSmithing data, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();
         if (player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.isCreative() ? data.carried() : player.containerMenu.getCarried();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.openMenu(new SmithingMenuProvider(data.accessPos()));
            if (!stack.isEmpty()) {
               if (!player.isCreative()) {
                  player.containerMenu.setCarried(stack);
               }

               serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket(-1, serverPlayer.containerMenu.incrementStateId(), -1, stack));
            }
         }
      });
   }

   public void handleOpenShieldSmithing(CPacketOpenShieldSmithing data, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();
         if (player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.isCreative() ? data.carried() : player.containerMenu.getCarried();
            player.containerMenu.setCarried(ItemStack.EMPTY);
            player.openMenu(new ShieldSmithingMenuProvider(data.accessPos()));
            if (!stack.isEmpty()) {
               player.containerMenu.setCarried(stack);
               serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket(-1, serverPlayer.containerMenu.incrementStateId(), -1, stack));
            }
         }
      });
   }

   public void handleIntAttachmentSync(PacketIntAttachment packetIntAttachment, IPayloadContext ctx) {
      ctx.enqueueWork(() -> {
         Player player = ctx.player();
         if (player.level().isClientSide) {
            Entity entity = player.level().getEntity(packetIntAttachment.id());
            if (entity != null) {
               entity.setData(packetIntAttachment.attachmentType(), packetIntAttachment.data());
            }
         }
      });
   }
}
