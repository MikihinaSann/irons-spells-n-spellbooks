package com.mumu17.ironsarms.network;

import com.mumu17.ironsarms.utils.GunTags;
import com.tacz.guns.api.item.IGun;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestSyncChargedManaMessage implements CustomPacketPayload {
    private final int manaCount;
    public static final String MANA = "ironsarms:Mana";
    public static final int MAX_MANA = 10000;

    public static final CustomPacketPayload.Type<RequestSyncChargedManaMessage> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ironsarms", "sync_charged_mana"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestSyncChargedManaMessage> STREAM_CODEC =
            CustomPacketPayload.codec(RequestSyncChargedManaMessage::write, RequestSyncChargedManaMessage::new);

    public RequestSyncChargedManaMessage(int ManaCount) {
        this.manaCount = ManaCount;
    }

    private RequestSyncChargedManaMessage(FriendlyByteBuf buf) {
        this.manaCount = buf.readInt();
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeInt(this.manaCount);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestSyncChargedManaMessage msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                for (ItemStack stack : player.getInventory().items) {
                    if (!stack.isEmpty() && stack.getItem() instanceof IGun) {
                        int chargedManaCount = GunTags.getMana(stack);
                        int removeManaCount = (int) ((float) msg.manaCount - (float) chargedManaCount);
                        if ((double) removeManaCount > 0.0) {
                            MagicData.getPlayerMagicData(player).addMana((float) (-removeManaCount));
                        }

                        GunTags.addMana(stack, removeManaCount);
                        break;
                    }
                }
            }
        });
    }
}
