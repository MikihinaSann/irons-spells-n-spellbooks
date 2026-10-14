package com.gametechbc.traveloptics.item.spellbook;

import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.init.TOSpells;
import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.MinecraftInstanceHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public class TheAccusedCodexSpellbook extends UniqueSpellBook {
   public TheAccusedCodexSpellbook() {
      super(
         SpellDataRegistryHolder.of(new SpellDataRegistryHolder[]{new SpellDataRegistryHolder(TOSpells.HALBERD_HORIZON_SPELL, 6)}),
         (Integer)SpellsConfig.theAccusedCodexSpellSlots.get()
      );
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, (Double)SpellsConfig.theAccusedCodexMaxMana.get(), Operation.ADD_VALUE),
            new AttributeContainer(
               AttributeRegistry.COOLDOWN_REDUCTION, (Double)SpellsConfig.theAccusedCodexCooldownReduction.get(), Operation.ADD_MULTIPLIED_BASE
            ),
            new AttributeContainer(AttributeRegistry.ICE_SPELL_POWER, (Double)SpellsConfig.theAccusedCodexIceSpellPower.get(), Operation.ADD_MULTIPLIED_BASE)
         }
      );
   }

   public void appendHoverText(@NotNull ItemStack itemStack, TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(itemStack);
      AbstractSpell spell = affinityData.getSpell();
      if (spell != SpellRegistry.none()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.irons_spellbooks.spellbook_spell_count");
         lines.add(
            i < 0 ? lines.size() : i + 1,
            Component.translatable(
                  "tooltip.irons_spellbooks.enhance_spell_level",
                  new Object[]{spell.getDisplayName(MinecraftInstanceHelper.instance.player()).withStyle(spell.getSchoolType().getDisplayName().getStyle())}
               )
               .withStyle(ChatFormatting.YELLOW)
         );
      }
   }
}
