package net.alshanex.familiarslib.item;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFamiliarSpellbookItem extends UniqueSpellBook {
   private static final Component DESCRIPTION = Component.translatable("item.familiarslib.familiar_spellbook.desc")
      .withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC});

   public AbstractFamiliarSpellbookItem() {
      super(SpellDataRegistryHolder.of(new SpellDataRegistryHolder[0]), 10);
      this.withSpellbookAttributes(this.getSpellbookAttributes());
   }

   protected AttributeContainer[] getSpellbookAttributes() {
      return new AttributeContainer[]{
         new AttributeContainer(AttributeRegistry.MAX_MANA, 200.0, Operation.ADD_VALUE),
         new AttributeContainer(AttributeRegistry.CAST_TIME_REDUCTION, 0.12, Operation.ADD_MULTIPLIED_BASE),
         new AttributeContainer(AttributeRegistry.SPELL_POWER, 0.08, Operation.ADD_MULTIPLIED_BASE)
      };
   }

   public void appendHoverText(@NotNull ItemStack itemStack, TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      lines.add(DESCRIPTION);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
      }
   }
}
