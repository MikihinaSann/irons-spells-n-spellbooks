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

public class ArchiveOfAbyssalSecretsSpellbook extends UniqueSpellBook {
   public ArchiveOfAbyssalSecretsSpellbook() {
      super(
         SpellDataRegistryHolder.of(
            new SpellDataRegistryHolder[]{
               new SpellDataRegistryHolder(TOSpells.ABYSSAL_BLAST_SPELL, 3), new SpellDataRegistryHolder(TOSpells.ORBITAL_VOID_SPELL, 5)
            }
         ),
         (Integer)SpellsConfig.abyssalSecretsSpellSlots.get()
      );
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.MAX_MANA, (Double)SpellsConfig.abyssalSecretsMaxMana.get(), Operation.ADD_VALUE),
            new AttributeContainer(AttributeRegistry.ENDER_SPELL_POWER, (Double)SpellsConfig.abyssalSecretsEnderSpellPower.get(), Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(
               AttributeRegistry.ELDRITCH_SPELL_POWER, (Double)SpellsConfig.abyssalSecretsEldritchSpellPower.get(), Operation.ADD_MULTIPLIED_BASE
            )
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

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.setAffinityData(itemStack, TOSpells.CURSED_MINEFIELD_SPELL.get());
      }
   }
}
