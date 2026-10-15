package net.hazen.hazennstuff.Item.Curios.Spellbooks;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public class GoldenShowerSpellbook extends UniqueSpellBook {
   public GoldenShowerSpellbook() {
      super(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(HnSSpellRegistries.GOLDEN_SHOWER, 3)}, 7);
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.HOLY_SPELL_POWER, 0.1, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE)
         }
      );
   }

   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(stack);
      if (!affinityData.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.hazennstuff.spellbook_spell_count");
         lines.addAll(i < 0 ? lines.size() : i + 1, affinityData.getDescriptionComponent());
      }

      lines.add(
         Component.translatable("item.hazennstuff.golden_shower_spellbook.description")
            .withStyle(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.ITALIC})
      );
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.set(itemStack, new AffinityData(Map.of(HnSSpellRegistries.GOLDEN_SHOWER.get().getSpellResource(), 1)));
      }
   }
}
