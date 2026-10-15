package net.acetheeldritchking.aces_spell_utils.items.weapons;

import io.redspace.ironsspellbooks.api.item.weapons.MagicSwordItem;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class MagicGunItem extends MagicSwordItem {
   public MagicGunItem(Tier pTier, Properties pProperties, SpellDataRegistryHolder[] spellDataRegistryHolders) {
      super(pTier, pProperties, spellDataRegistryHolders);
   }

   protected int getPassiveCooldownTicks() {
      return 0;
   }

   protected int getActiveCooldownTicks() {
      return 0;
   }

   public boolean isHeavyGun() {
      return true;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
      super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
      if (Screen.hasShiftDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "tooltip.irons_spellbooks.passive_ability",
                  new Object[]{Component.literal(Utils.timeFromTicks(this.getPassiveCooldownTicks(), 1)).withStyle(ChatFormatting.LIGHT_PURPLE)}
               )
               .withStyle(ChatFormatting.DARK_PURPLE)
         );
         tooltipComponents.add(Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(ChatFormatting.YELLOW));
      } else if (Screen.hasAltDown()) {
         tooltipComponents.add(
            Component.translatable(
                  "tooltip.aces_spell_utils.active_ability",
                  new Object[]{Component.literal(Utils.timeFromTicks(this.getActiveCooldownTicks(), 1)).withStyle(ChatFormatting.LIGHT_PURPLE)}
               )
               .withStyle(ChatFormatting.DARK_PURPLE)
         );
         tooltipComponents.add(Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc2")).withStyle(ChatFormatting.YELLOW));
      } else {
         tooltipComponents.add(Component.translatable("item.aces_spell_utils.more_details1").withStyle(ChatFormatting.GRAY));
         tooltipComponents.add(Component.translatable("item.aces_spell_utils.more_details2").withStyle(ChatFormatting.GRAY));
         if (this.isHeavyGun()) {
            tooltipComponents.add(Component.translatable("item.aces_spell_utils.heavy_gun_info").withStyle(ChatFormatting.RED));
         } else {
            tooltipComponents.add(Component.translatable("item.aces_spell_utils.light_gun_info").withStyle(ChatFormatting.GOLD));
         }
      }
   }
}
