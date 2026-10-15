package com.chadate.spellelemental.event;

import com.chadate.spellelemental.util.ActionBarMessageUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

@EventBusSubscriber(
   modid = "spellelemental"
)
public class GrindstoneElementClearingHandler {
   @SubscribeEvent
   public static void onPlayerRightClickBlock(RightClickBlock event) {
      Player player = event.getEntity();
      ItemStack heldItem = player.getItemInHand(event.getHand());
      if (heldItem.getItem() instanceof SwordItem && player.isShiftKeyDown() && event.getLevel().getBlockState(event.getPos()).getBlock() == Blocks.GRINDSTONE) {
         if (!event.getLevel().isClientSide()) {
            CompoundTag tag = ((CustomData)heldItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            CompoundTag elementData = tag.getCompound("ElementAttachment");
            if (elementData.contains("element")) {
               String elementType = elementData.getString("element");
               int elementAmount = elementData.getInt("amount");
               tag.remove("ElementAttachment");
               heldItem.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(
                     serverPlayer,
                     "message.spellelemental.sword_oil.element_cleared",
                     Component.translatable("element.spellelemental." + elementType),
                     elementAmount
                  );
               }

               event.getLevel().playSound(null, event.getPos(), SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
               event.setCanceled(true);
            } else {
               if (player instanceof ServerPlayer serverPlayer) {
                  ActionBarMessageUtil.sendTranslatableActionBarMessage(serverPlayer, "message.spellelemental.sword_oil.no_element_to_clear");
               }

               event.setCanceled(true);
            }
         }
      }
   }
}
