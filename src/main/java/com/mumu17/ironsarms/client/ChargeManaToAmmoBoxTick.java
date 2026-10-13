package com.mumu17.ironsarms.client;

import com.mumu17.ironsarms.network.RequestSyncChargedManaMessage;
import com.mumu17.ironsarms.utils.GunTags;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "ironsarms", value = {Dist.CLIENT})
public class ChargeManaToAmmoBoxTick {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null) {
                for (ItemStack stack : player.getInventory().items) {
                    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                    if (GunTags.isTargetItem(stack) && data != null && data.copyTag().contains(GunTags.INSCRIBED_SPELL_KEY)) {
                        chargeManaOrCancel(stack);
                        return;
                    }
                }
            }
        }
    }

    private static void chargeManaOrCancel(ItemStack stack) {
        int chargeMinMana = 100;
        int chargedManaCount = GunTags.getMana(stack);
        if (chargedManaCount < 0) {
            chargedManaCount = 0;
        }

        double mana = (double) ClientMagicData.getPlayerMana();
        if (!(mana < (double) chargeMinMana)) {
            int maxManaCount = 10000;
            int maxChargedManaCount = maxManaCount - chargedManaCount;
            int chargeManaCount = Math.min(maxChargedManaCount, chargeMinMana);
            if (chargeManaCount > 0) {
                sendManaCountToServer(Math.min(chargedManaCount + chargeManaCount, maxManaCount));
            }
        }
    }

    public static void sendManaCountToServer(int manaCount) {
        ClientMagicData.setMana(manaCount);
        PacketDistributor.sendToServer(new RequestSyncChargedManaMessage(manaCount));
    }
}
