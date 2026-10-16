package com.userofbricks.expanded_combat.client;

import com.userofbricks.expanded_combat.ExpandedCombat;
import com.userofbricks.expanded_combat.api.client.IGauntletRenderer;
import com.userofbricks.expanded_combat.client.renderer.gui.screen.inventory.ShieldSmithingTableScreen;
import com.userofbricks.expanded_combat.client.renderer.gui.screen.inventory.ShieldTabButtion;
import com.userofbricks.expanded_combat.init.ECEnchantments;
import com.userofbricks.expanded_combat.item.GauntletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Background;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.client.event.ScreenEvent.Init.Post;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/**
 * Client-only game-bus handlers split out of {@code GauntletEvents},
 * {@code EnchantentEvents} and {@code ShieldEvents}: they reference client-only
 * classes ({@code AbstractClientPlayer}, screens, {@code Minecraft}) so they
 * cannot live in classes that must load on a dedicated server.
 */
@EventBusSubscriber(
   modid = "expanded_combat",
   bus = Bus.GAME,
   value = {Dist.CLIENT}
)
public class ECClientGameEvents {

   @SubscribeEvent
   public static void onRenderArm(RenderArmEvent event) {
      CuriosApi.getCuriosInventory(event.getPlayer())
         .ifPresent(
            handler -> {
               ICurioStacksHandler stacksHandler = (ICurioStacksHandler)handler.getCurios().get("hands");
               if (stacksHandler != null) {
                  IDynamicStackHandler stacks = stacksHandler.getStacks();
                  IDynamicStackHandler cosmeticStacks = stacksHandler.getCosmeticStacks();
                  ItemStack stack = cosmeticStacks.getStackInSlot(0);
                  if (stack.isEmpty() && (Boolean)stacksHandler.getRenders().get(0)) {
                     stack = stacks.getStackInSlot(0);
                  }

                  if (stack.getItem() instanceof GauntletItem gauntletItem) {
                     ICurioRenderer iCurioRenderer = gauntletItem.getGauntletRenderer().get();
                     if (iCurioRenderer instanceof IGauntletRenderer gauntletRenderer) {
                        gauntletRenderer.renderFirstPersonArm(
                           stack,
                           event.getPoseStack(),
                           event.getMultiBufferSource(),
                           event.getPackedLight(),
                           event.getPlayer(),
                           event.getArm(),
                           stack.hasFoil()
                        );
                     }
                  }
               }
            }
         );
   }

   @SubscribeEvent
   public static void agilityAttributesEvent(ItemAttributeModifierEvent event) {
      ItemStack stack = event.getItemStack();
      if (Minecraft.getInstance().level != null) {
         Registry<Enchantment> enchantmentRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
         if (stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.AGILITY)) > 0 && stack.getItem() instanceof ArmorItem armorItem) {
            int level = stack.getEnchantmentLevel(enchantmentRegistry.getHolderOrThrow(ECEnchantments.AGILITY));
            if (armorItem.getEquipmentSlot() == EquipmentSlot.FEET) {
               event.addModifier(
                  Attributes.MOVEMENT_SPEED,
                  new AttributeModifier(ExpandedCombat.modLoc("movement_speed"), level * 0.2, Operation.ADD_MULTIPLIED_BASE),
                  EquipmentSlotGroup.FEET
               );
            }

            if (armorItem.getEquipmentSlot() == EquipmentSlot.LEGS) {
               event.addModifier(
                  NeoForgeMod.SWIM_SPEED,
                  new AttributeModifier(ExpandedCombat.modLoc("jump_strength"), level * 0.2, Operation.ADD_MULTIPLIED_BASE),
                  EquipmentSlotGroup.LEGS
               );
            }
         }
      }
   }

   @SubscribeEvent
   public static void onInventoryGuiInit(Post evt) {
      if (ExpandedCombat.CONFIG.enableShields) {
         Screen screen = evt.getScreen();
         if (screen instanceof SmithingScreen gui) {
            int sizeX = 20;
            int sizeY = 20;
            int yOffset = 36;
            int xOffset = -21;
            evt.addListener(new ShieldTabButtion(gui, gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset, sizeX, sizeY, ShieldTabButtion.SHIELD));
         } else if (screen instanceof ShieldSmithingTableScreen gui) {
            int sizeX = 20;
            int sizeY = 20;
            int yOffset = 8;
            int xOffset = -21;
            evt.addListener(new ShieldTabButtion(gui, gui.getGuiLeft() + xOffset, gui.getGuiTop() + yOffset, sizeX, sizeY, ShieldTabButtion.HAMMER));
         }
      }
   }

   @SubscribeEvent
   public static void drawTabs(Background e) {
      if (ExpandedCombat.CONFIG.enableShields) {
         if (e.getContainerScreen() instanceof SmithingScreen) {
            AbstractContainerScreen<?> smithingTableScreen = e.getContainerScreen();
            int left = smithingTableScreen.getGuiLeft();
            int top = smithingTableScreen.getGuiTop();
            e.getGuiGraphics().blit(ShieldSmithingTableScreen.SHIELD_SMITHING_LOCATION, left - 28, top + 4, 0, 194, 32, 28);
            e.getGuiGraphics().blit(ShieldSmithingTableScreen.SHIELD_SMITHING_LOCATION, left - 28, top + 32, 0, 166, 32, 28);
            e.getGuiGraphics().blitSprite(ExpandedCombat.modLoc("smithing_tab"), left - 23, top + 8, 20, 20);
         } else if (e.getContainerScreen() instanceof ShieldSmithingTableScreen smithingTableScreen) {
            int left = smithingTableScreen.getGuiLeft();
            int top = smithingTableScreen.getGuiTop();
            e.getGuiGraphics().blit(ShieldSmithingTableScreen.SHIELD_SMITHING_LOCATION, left - 28, top + 4, 0, 166, 32, 56);
            e.getGuiGraphics().blitSprite(ExpandedCombat.modLoc("shield_smithing_tab"), left - 23, top + 36, 20, 20);
         }
      }
   }
}
