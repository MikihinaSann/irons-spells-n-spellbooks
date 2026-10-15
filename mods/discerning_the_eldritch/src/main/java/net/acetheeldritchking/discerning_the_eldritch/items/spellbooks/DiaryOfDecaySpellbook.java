package net.acetheeldritchking.discerning_the_eldritch.items.spellbooks;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.item.weapons.AttributeContainer;
import io.redspace.ironsspellbooks.util.ItemPropertiesHelper;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import java.util.Map;
import net.acetheeldritchking.aces_spell_utils.items.curios.PassiveAbilitySpellbook;
import net.acetheeldritchking.aces_spell_utils.utils.ASRarities;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEAttributeRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public class DiaryOfDecaySpellbook extends PassiveAbilitySpellbook {
   public static final int COOLDOWN = 100;

   public DiaryOfDecaySpellbook() {
      super(12, ItemPropertiesHelper.equipment().fireResistant().stacksTo(1).rarity((Rarity)ASRarities.ACCURSED_RARITY_PROXY.getValue()));
      this.withSpellbookAttributes(
         new AttributeContainer[]{
            new AttributeContainer(AttributeRegistry.BLOOD_SPELL_POWER, 0.15F, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(AttributeRegistry.SPELL_RESIST, 0.05F, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(DTEAttributeRegistry.RITUAL_MAGIC_POWER, 0.05F, Operation.ADD_MULTIPLIED_BASE),
            new AttributeContainer(AttributeRegistry.MAX_MANA, 250.0, Operation.ADD_VALUE)
         }
      );
   }

   public void appendHoverText(@NotNull ItemStack itemStack, TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      AffinityData affinityData = AffinityData.getAffinityData(itemStack);
      if (!affinityData.affinityData().isEmpty()) {
         int i = TooltipsUtils.indexOfComponent(lines, "tooltip.irons_spellbooks.spellbook_spell_count");
         lines.addAll(i < 0 ? lines.size() : i + 1, affinityData.getDescriptionComponent());
      }
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
         AffinityData.set(
            itemStack,
            new AffinityData(
               Map.of(
                  SpellRegistries.BLADES_OF_RANCOR.get().getSpellResource(),
                  1,
                  ((AbstractSpell)SpellRegistry.ACUPUNCTURE_SPELL.get()).getSpellResource(),
                  1,
                  SpellRegistries.VEIN_RIPPER.get().getSpellResource(),
                  1
               )
            )
         );
      }
   }

   protected int getCooldownTicks() {
      return 100;
   }
}
