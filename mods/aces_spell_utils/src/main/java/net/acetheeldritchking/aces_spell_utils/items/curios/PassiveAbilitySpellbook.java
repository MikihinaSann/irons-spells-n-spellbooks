package net.acetheeldritchking.aces_spell_utils.items.curios;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class PassiveAbilitySpellbook extends SpellBook {
   Style description;
   protected final int maxSpellSlots;

   public int getMaxSpellSlots() {
      return this.maxSpellSlots;
   }

   public PassiveAbilitySpellbook() {
      this(1);
      this.description = Style.EMPTY.withColor(ChatFormatting.YELLOW);
   }

   public PassiveAbilitySpellbook(int maxSpellSlots) {
      this(maxSpellSlots, ItemPropertiesHelper.equipment().stacksTo(1).rarity(Rarity.UNCOMMON));
      this.description = Style.EMPTY.withColor(ChatFormatting.YELLOW);
   }

   public PassiveAbilitySpellbook(int maxSpellSlots, Properties pProperties) {
      super(maxSpellSlots, pProperties);
      this.maxSpellSlots = maxSpellSlots;
      this.description = Style.EMPTY.withColor(ChatFormatting.YELLOW);
   }

   public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
      List<Component> tooltip = super.getAttributesTooltip(tooltips, context, stack);
      boolean needsHeader = tooltip.isEmpty();
      List<Component> descLines = this.getDescriptionLines(stack);
      if (needsHeader && !descLines.isEmpty()) {
         tooltip.add(Component.empty());
      }

      tooltip.addAll(descLines);
      return tooltip;
   }

   protected int getCooldownTicks() {
      return 0;
   }

   public boolean tryProcCooldown(Player player) {
      if (player.getCooldowns().isOnCooldown(this)) {
         return false;
      } else {
         player.getCooldowns().addCooldown(this, this.getCooldownTicks());
         return true;
      }
   }

   public List<Component> getDescriptionLines(ItemStack stack) {
      return List.of(
         Component.translatable(
               "tooltip.irons_spellbooks.passive_ability",
               new Object[]{Component.literal(Utils.timeFromTicks(this.getCooldownTicks(), 1)).withStyle(ChatFormatting.AQUA)}
            )
            .withStyle(ChatFormatting.GREEN),
         this.getDescription(stack)
      );
   }

   public Component getDescription(ItemStack stack) {
      return Component.literal(" ").append(Component.translatable(this.getDescriptionId() + ".desc")).withStyle(this.description);
   }
}
