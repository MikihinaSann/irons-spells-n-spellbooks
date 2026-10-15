package com.somake.somakespells.event;

import com.somake.somakespells.capabilities.PlayerSpellMastery;
import com.somake.somakespells.client.ElementalFootstepHandler;
import com.somake.somakespells.network.FireOrbsAttackPacket;
import com.somake.somakespells.registries.ModAttachments;
import com.somake.somakespells.registries.ModCompatItems;
import com.somake.somakespells.registries.ModItems;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.spells.SpellSlot;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent.Post;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "somakespells",
   value = {Dist.CLIENT},
   bus = Bus.GAME
)
public class ClientMagicHandler {
   private static final int FIRE_PRIMARY = 16738816;
   private static final int FIRE_ACCENT = 16766976;
   private static final int SOUL_FIRE_PRIMARY = 106404;
   private static final int SOUL_FIRE_ACCENT = 5500916;
   private static final int INFERNAL_FIRE_PRIMARY = 7844643;
   private static final int INFERNAL_FIRE_ACCENT = 10542383;
   private static final Style OBFUSCATED_STYLE = Style.EMPTY.withObfuscated(true).withFont(ResourceLocation.withDefaultNamespace("alt"));
   private static boolean wasAttackKeyDown;

   @SubscribeEvent
   public static void onItemTooltip(ItemTooltipEvent event) {
      Player player = event.getEntity();
      if (player != null) {
         ItemStack stack = event.getItemStack();
         if (stack.getItem() == ModItems.FIRE.get()) {
            addElementalFireTooltip(event, stack, "item.somakespells.fire", 16738816, 16766976);
         } else if (stack.getItem() == ModItems.SOUL_FIRE.get()) {
            addElementalFireTooltip(event, stack, "item.somakespells.soul_fire", 106404, 5500916);
         } else if (ModCompatItems.isInfernalFireItem(stack.getItem())) {
            addElementalFireTooltip(event, stack, "item.somakespells.infernal_fire", 7844643, 10542383);
         } else if (stack.getItem() instanceof Scroll || stack.getItem() instanceof SpellBook) {
            ISpellContainer container = ISpellContainer.get(stack);
            if (container != null && !container.isEmpty()) {
               PlayerSpellMastery mastery = (PlayerSpellMastery)player.getData(ModAttachments.PLAYER_SPELL_MASTERY);
               if (stack.getItem() instanceof Scroll) {
                  SpellData spellData = container.getSpellAtIndex(0);
                  if (spellData != null) {
                     checkAndObfuscate(event, mastery, spellData.getSpell(), spellData.getLevel());
                  }
               } else if (stack.getItem() instanceof SpellBook) {
                  SpellSlot[] spells = container.getAllSpells();

                  for (SpellSlot slot : spells) {
                     if (slot != null) {
                        AbstractSpell spell = slot.getSpell();
                        if (spell != null && !spell.getSpellId().equals("irons_spellbooks:none")) {
                           int lvl = slot.getLevel();
                           if (lvl > mastery.getUnlockedLevel(spell.getSpellId())) {
                              event.getToolTip().add(Component.translatable("ui.irons_spellbooks.unlearned_error").withStyle(ChatFormatting.RED));
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerTick(Post event) {
      if (event.getEntity() == Minecraft.getInstance().player) {
         ElementalFootstepHandler.tickLocalPlayer();
         boolean attackKeyDown = Minecraft.getInstance().options.keyAttack.isDown();
         if (attackKeyDown && !wasAttackKeyDown) {
            PacketDistributor.sendToServer(new FireOrbsAttackPacket(), new CustomPacketPayload[0]);
         }

         wasAttackKeyDown = attackKeyDown;
      }
   }

   private static void checkAndObfuscate(ItemTooltipEvent event, PlayerSpellMastery mastery, AbstractSpell spell, int level) {
      if (level > mastery.getUnlockedLevel(spell.getSpellId())) {
         event.getToolTip().add(1, Component.translatable("ui.irons_spellbooks.unlearned_error").withStyle(ChatFormatting.RED));
      }
   }

   private static void addElementalFireTooltip(ItemTooltipEvent event, ItemStack stack, String baseKey, int primaryColor, int accentColor) {
      if (!event.getToolTip().isEmpty()) {
         event.getToolTip().set(0, stack.getHoverName().copy().withStyle(style -> style.withColor(primaryColor).withBold(true)));
      }

      event.getToolTip().add(Component.empty());
      event.getToolTip().add(Component.translatable(baseKey + ".desc.1").withStyle(style -> style.withColor(accentColor)));
      event.getToolTip().add(Component.translatable(baseKey + ".desc.2").withStyle(style -> style.withColor(primaryColor)));
      event.getToolTip()
         .add(
            Component.translatable(baseKey + ".desc.3.prefix")
               .withStyle(style -> style.withColor(accentColor))
               .append(Component.translatable(baseKey + ".desc.3.bonus").withStyle(style -> style.withColor(primaryColor)))
               .append(Component.translatable(baseKey + ".desc.3.suffix").withStyle(style -> style.withColor(accentColor)))
         );
   }
}
