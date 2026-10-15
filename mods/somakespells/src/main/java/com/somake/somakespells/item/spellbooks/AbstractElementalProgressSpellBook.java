package com.somake.somakespells.item.spellbooks;

import io.redspace.ironsspellbooks.api.item.curios.AffinityData;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractElementalProgressSpellBook extends SpellBook {
   private final ResourceLocation trackedSchoolId;
   private final String progressionFamilyId;
   private final int progressionTier;
   private final AbstractElementalProgressSpellBook.ConditionalObjectiveKind conditionalObjectiveKind;
   private final SpellbookProgressHelper.ProgressTheme progressTheme;

   protected AbstractElementalProgressSpellBook(
      int maxSpellSlots,
      ResourceLocation trackedSchoolId,
      String progressionFamilyId,
      int progressionTier,
      AbstractElementalProgressSpellBook.ConditionalObjectiveKind conditionalObjectiveKind,
      SpellbookProgressHelper.ProgressTheme progressTheme
   ) {
      super(maxSpellSlots);
      this.trackedSchoolId = trackedSchoolId;
      this.progressionFamilyId = progressionFamilyId;
      this.progressionTier = progressionTier;
      this.conditionalObjectiveKind = conditionalObjectiveKind;
      this.progressTheme = progressTheme;
   }

   public ResourceLocation getTrackedSchoolId() {
      return this.trackedSchoolId;
   }

   public String getProgressionFamilyId() {
      return this.progressionFamilyId;
   }

   public int getProgressionTier() {
      return this.progressionTier;
   }

   public AbstractElementalProgressSpellBook.ConditionalObjectiveKind getConditionalObjectiveKind() {
      return this.conditionalObjectiveKind;
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      return super.getName(stack).copy().withStyle(Style.EMPTY.withColor(this.progressTheme.titleColor()));
   }

   public void appendHoverText(@NotNull ItemStack itemStack, TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag) {
      super.appendHoverText(itemStack, context, lines, flag);
      int insertionIndex = TooltipsUtils.indexOfComponent(lines, "tooltip.irons_spellbooks.spellbook_spell_count");
      insertionIndex = insertionIndex < 0 ? lines.size() : insertionIndex + 1;
      AffinityData affinityData = AffinityData.getAffinityData(itemStack);
      if (!affinityData.affinityData().isEmpty()) {
         List<MutableComponent> affinityLines = affinityData.getDescriptionComponent();
         lines.addAll(insertionIndex, affinityLines);
         insertionIndex += affinityLines.size();
      }

      SpellbookProgressHelper.appendTooltip(itemStack, lines, insertionIndex, this, this.progressTheme);
   }

   public void initializeSpellContainer(ItemStack itemStack) {
      if (itemStack != null) {
         super.initializeSpellContainer(itemStack);
      }
   }

   public static enum ConditionalObjectiveKind {
      ANY,
      BURNING,
      WET,
      UNDEAD,
      ENDER,
      ILLAGER,
      ELECTROCUTED,
      FROZEN,
      POISONED;
   }
}
