package com.gametechbc.traveloptics.item.bossweapon.cursed_wraithblade;

import com.gametechbc.traveloptics.data_manager.SpiritPointsManager;
import com.gametechbc.traveloptics.data_manager.SwingCounterManager;
import com.gametechbc.traveloptics.init.TOSpells;
import com.gametechbc.traveloptics.item.TOWeaponTier;
import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public class CursedWraithbladeItem extends MagicSwordItem {
   private static final String SWING_COUNTER_TAG = "SwingCounter";

   public CursedWraithbladeItem() {
      super(
         TOWeaponTier.INFERNAL_DEVASTATOR,
         ItemPropertiesHelper.equipment().rarity(Rarity.EPIC).attributes(ExtendedSwordItem.createAttributes(TOWeaponTier.INFERNAL_DEVASTATOR)),
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(TOSpells.CURSED_BLAST_SPELL, 1)})
      );
   }

   public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (attacker instanceof Player player) {
         int swingCounter = SwingCounterManager.getSwingCount(stack);

         SpiritPointsManager.addSpiritPoints(stack, player, switch (swingCounter) {
            case 1 -> 5;
            case 2 -> 10;
            case 3 -> 15;
            default -> 5;
         });
         SwingCounterManager.incrementSwingCount(stack, 3);
         MagicManager.spawnParticles(target.level(), ParticleTypes.SCULK_SOUL, target.getX(), target.getY(), target.getZ(), 30, 0.0, 0.0, 0.0, 0.3, false);
      }

      return super.hurtEnemy(stack, target, attacker);
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
      tooltip.add(Component.literal(""));
      tooltip.add(Component.translatable("item.traveloptics.cursed_wraithblade.tooltip").withStyle(ChatFormatting.GREEN));
      tooltip.add(Component.literal("☠ Soul Fragments: " + SpiritPointsManager.getSpiritPoints(stack)));
      tooltip.add(Component.translatable("item.traveloptics.cursed_wraithblade.tooltip1"));
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.literal(""));
         tooltip.add(Component.translatable("item.traveloptics.weapons.evolution_benefits.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.cursed_wraithblade.evo_one.inactive.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.cursed_wraithblade.evo_two.inactive.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.cursed_wraithblade.evo_three.inactive.tooltip"));
         tooltip.add(Component.literal(""));
      } else {
         tooltip.add(Component.translatable("item.traveloptics.weapons.evolution.stars_zero.tooltip"));
         tooltip.add(Component.translatable("item.traveloptics.shift_hold.advanced_tooltips"));
      }

      tooltip.add(Component.literal(""));
   }
}
