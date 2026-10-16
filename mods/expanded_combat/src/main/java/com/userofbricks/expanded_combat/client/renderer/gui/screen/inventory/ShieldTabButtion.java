package com.userofbricks.expanded_combat.client.renderer.gui.screen.inventory;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.inventory.container.ShieldSmithingMenu;
import com.userofbricks.expanded_combat.network.client.CPacketOpenShieldSmithing;
import com.userofbricks.expanded_combat.network.client.CPacketOpenSmithing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class ShieldTabButtion extends ImageButton {
   public static final WidgetSprites SHIELD = new WidgetSprites(ExpandedCombat.modLoc("shield_smithing_tab"), ExpandedCombat.modLoc("shield_smithing_tab"));
   public static final WidgetSprites HAMMER = new WidgetSprites(ExpandedCombat.modLoc("smithing_tab"), ExpandedCombat.modLoc("smithing_tab"));

   public ShieldTabButtion(AbstractContainerScreen<?> parentGui, int xIn, int yIn, int widthIn, int heightIn, WidgetSprites sprites) {
      super(xIn, yIn, widthIn, heightIn, sprites, button -> {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            ItemStack stack = mc.player.containerMenu.getCarried().copy();
            mc.player.containerMenu.setCarried(ItemStack.EMPTY);
            if (parentGui instanceof ShieldSmithingTableScreen screen) {
               mc.player.containerMenu.setCarried(stack);
               BlockPos blockPos = (BlockPos)((ShieldSmithingMenu)screen.getMenu()).access.evaluate((level, pos) -> pos).orElseGet(mc.player::blockPosition);
               PacketDistributor.sendToServer(new CPacketOpenSmithing(stack, blockPos), new CustomPacketPayload[0]);
            } else if (parentGui instanceof SmithingScreen screen) {
               mc.player.containerMenu.setCarried(stack);
               BlockPos blockPos = (BlockPos)((SmithingMenu)screen.getMenu()).access.evaluate((level, pos) -> pos).orElseGet(mc.player::blockPosition);
               PacketDistributor.sendToServer(new CPacketOpenShieldSmithing(stack, blockPos), new CustomPacketPayload[0]);
            }
         }
      });
   }
}
