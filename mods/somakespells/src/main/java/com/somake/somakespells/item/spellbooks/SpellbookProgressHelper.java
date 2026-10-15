package com.somake.somakespells.item.spellbooks;

import com.somake.somakespells.config.progression.SpellbookProgressionConfig;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class SpellbookProgressHelper {
   private static final String ROOT_KEY = "somake_spellbook_progress";
   private static final String FAMILY_KEY = "family";
   private static final String TIER_KEY = "tier";
   private static final String XP_KEY = "progress";
   private static final String STAGE_KEY = "stage";
   private static final ResourceLocation WET_EFFECT_ID = ResourceLocation.fromNamespaceAndPath("somakespells", "wet");
   private static final ResourceLocation ELECTROCUTED_EFFECT_ID = ResourceLocation.fromNamespaceAndPath("somakespells", "electrocuted");
   private static final ResourceLocation OVERGROWN_EFFECT_ID = ResourceLocation.fromNamespaceAndPath("somakespells", "overgrown");
   private static final Map<String, SpellbookProgressHelper.ProgressTheme> THEMES = Map.ofEntries(
      Map.entry(
         "burning_magma",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.burning_magma.title"),
            Component.translatable("tooltip.somakespells.progress.burning_magma.track"),
            List.of(),
            16757575,
            16742938,
            6958096,
            16769187,
            13131050
         )
      ),
      Map.entry(
         "tidebound",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.tidebound.title"),
            Component.translatable("tooltip.somakespells.progress.tidebound.track"),
            List.of(),
            9431295,
            3909577,
            2775152,
            14219007,
            6608639
         )
      ),
      Map.entry(
         "starless_grimoire",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.starless_grimoire.title"),
            Component.translatable("tooltip.somakespells.progress.starless_grimoire.track"),
            List.of(),
            9429211,
            3116934,
            1985866,
            14155771,
            7327941
         )
      ),
      Map.entry(
         "voidbound_codex",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.voidbound_codex.title"),
            Component.translatable("tooltip.somakespells.progress.voidbound_codex.track"),
            List.of(),
            13939711,
            9133254,
            4531808,
            15853311,
            12159743
         )
      ),
      Map.entry(
         "invoker_canon",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.invoker_canon.title"),
            Component.translatable("tooltip.somakespells.progress.invoker_canon.track"),
            List.of(),
            15790320,
            12105912,
            5723991,
            16777215,
            13684944
         )
      ),
      Map.entry(
         "dawnlit_gospel",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.dawnlit_gospel.title"),
            Component.translatable("tooltip.somakespells.progress.dawnlit_gospel.track"),
            List.of(),
            16774084,
            14070874,
            7232551,
            16776165,
            16175723
         )
      ),
      Map.entry(
         "frostvein_codex",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.frostvein_codex.title"),
            Component.translatable("tooltip.somakespells.progress.frostvein_codex.track"),
            List.of(),
            14283775,
            7256550,
            3037550,
            15858687,
            10147839
         )
      ),
      Map.entry(
         "stormcaller_lexicon",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.stormcaller_lexicon.title"),
            Component.translatable("tooltip.somakespells.progress.stormcaller_lexicon.track"),
            List.of(),
            15398143,
            8040191,
            3165816,
            16317695,
            10405375
         )
      ),
      Map.entry(
         "verdant_testament",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.verdant_testament.title"),
            Component.translatable("tooltip.somakespells.progress.verdant_testament.track"),
            List.of(),
            11463300,
            5151313,
            3104304,
            15335385,
            8313964
         )
      ),
      Map.entry(
         "sanguine_testament",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.sanguine_testament.title"),
            Component.translatable("tooltip.somakespells.progress.sanguine_testament.track"),
            List.of(),
            15833497,
            12139580,
            5906207,
            16770019,
            14244956
         )
      ),
      Map.entry(
         "soullit_testament",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.soullit_testament.title"),
            Component.translatable("tooltip.somakespells.progress.soullit_testament.track"),
            List.of(),
            13170175,
            7521498,
            2908010,
            15596799,
            10479605
         )
      ),
      Map.entry(
         "axiomatic_codex",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.axiomatic_codex.title"),
            Component.translatable("tooltip.somakespells.progress.axiomatic_codex.track"),
            List.of(),
            12900351,
            7243736,
            3229295,
            15660031,
            10006256
         )
      ),
      Map.entry(
         "stoneheart_codex",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.stoneheart_codex.title"),
            Component.translatable("tooltip.somakespells.progress.stoneheart_codex.track"),
            List.of(),
            13219740,
            9138516,
            4864811,
            15721944,
            12098425
         )
      ),
      Map.entry(
         "resonance_canticle",
         new SpellbookProgressHelper.ProgressTheme(
            Component.translatable("tooltip.somakespells.progress.resonance_canticle.title"),
            Component.translatable("tooltip.somakespells.progress.resonance_canticle.track"),
            List.of(),
            15128063,
            10187737,
            5125742,
            16183039,
            12624368
         )
      )
   );

   private SpellbookProgressHelper() {
   }

   public static SpellbookProgressHelper.ProgressTheme fireTheme() {
      return theme("burning_magma");
   }

   public static SpellbookProgressHelper.ProgressTheme aquaTheme() {
      return theme("tidebound");
   }

   public static SpellbookProgressHelper.ProgressTheme eldritchTheme() {
      return theme("starless_grimoire");
   }

   public static SpellbookProgressHelper.ProgressTheme enderTheme() {
      return theme("voidbound_codex");
   }

   public static SpellbookProgressHelper.ProgressTheme evocationTheme() {
      return theme("invoker_canon");
   }

   public static SpellbookProgressHelper.ProgressTheme holyTheme() {
      return theme("dawnlit_gospel");
   }

   public static SpellbookProgressHelper.ProgressTheme iceTheme() {
      return theme("frostvein_codex");
   }

   public static SpellbookProgressHelper.ProgressTheme lightningTheme() {
      return theme("stormcaller_lexicon");
   }

   public static SpellbookProgressHelper.ProgressTheme natureTheme() {
      return theme("verdant_testament");
   }

   public static SpellbookProgressHelper.ProgressTheme bloodTheme() {
      return theme("sanguine_testament");
   }

   public static SpellbookProgressHelper.ProgressTheme spiritTheme() {
      return theme("soullit_testament");
   }

   public static SpellbookProgressHelper.ProgressTheme symmetryTheme() {
      return theme("axiomatic_codex");
   }

   public static SpellbookProgressHelper.ProgressTheme geoTheme() {
      return theme("stoneheart_codex");
   }

   public static SpellbookProgressHelper.ProgressTheme soundTheme() {
      return theme("resonance_canticle");
   }

   private static SpellbookProgressHelper.ProgressTheme theme(String familyId) {
      return THEMES.getOrDefault(familyId, THEMES.get("burning_magma"));
   }

   public static void recordSpellCast(ItemStack stack, AbstractElementalProgressSpellBook book) {
      if (stack != null && !stack.isEmpty() && book != null && hasFamilyConfig(book)) {
         if (book.getProgressionTier() < 3) {
            if (currentStage(stack, book) == 0) {
               incrementObjectiveProgress(stack, book, 1);
            }
         }
      }
   }

   public static void recordKill(ItemStack stack, AbstractElementalProgressSpellBook book, LivingEntity target) {
      if (stack != null && !stack.isEmpty() && book != null && target != null && hasFamilyConfig(book)) {
         if (book.getProgressionTier() < 3) {
            int stage = currentStage(stack, book);
            if (stage >= 1 && stage <= 3) {
               boolean matches = switch (stage) {
                  case 1 -> matchesConditionalObjective(book, target);
                  case 2 -> matchesMobId(target, valuesFor(book).targetMobId());
                  case 3 -> matchesMobId(target, valuesFor(book).finalMobId());
                  default -> false;
               };
               if (matches) {
                  incrementObjectiveProgress(stack, book, 1);
               }
            }
         }
      }
   }

   public static boolean hasCompletedAllTracks(ItemStack stack, ResourceLocation schoolId) {
      if (stack != null && !stack.isEmpty() && schoolId != null) {
         return !(stack.getItem() instanceof AbstractElementalProgressSpellBook book)
            ? false
            : book.getTrackedSchoolId().equals(schoolId) && readProgress(stack, book).maxed();
      } else {
         return false;
      }
   }

   public static void resetProgress(ItemStack stack) {
      if (!stack.isEmpty()) {
         CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove("somake_spellbook_progress"));
      }
   }

   public static ItemStack createMaxProgressPreview(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.getItem() instanceof AbstractElementalProgressSpellBook book && book.getProgressionTier() < 3) {
         ItemStack preview = stack.copy();
         CustomData.update(DataComponents.CUSTOM_DATA, preview, tag -> {
            CompoundTag progressTag = new CompoundTag();
            progressTag.putString("family", book.getProgressionFamilyId());
            progressTag.putInt("tier", book.getProgressionTier());
            progressTag.putInt("stage", 4);
            progressTag.putInt("progress", 0);
            tag.put("somake_spellbook_progress", progressTag);
         });
         return preview;
      } else {
         return stack.copy();
      }
   }

   public static void appendTooltip(
      ItemStack stack, List<Component> lines, int insertionIndex, AbstractElementalProgressSpellBook book, SpellbookProgressHelper.ProgressTheme theme
   ) {
      if (stack != null && lines != null && book != null && theme != null && hasFamilyConfig(book)) {
         if (book.getProgressionTier() < 3) {
            SpellbookProgressHelper.ProgressSnapshot snapshot = readProgress(stack, book);
            String progressText = snapshot.maxed() ? "MAX" : snapshot.currentXp() + " / " + snapshot.requiredXp();
            lines.addAll(
               insertionIndex,
               List.of(
                  Component.empty(),
                  theme.title().copy().withStyle(Style.EMPTY.withColor(theme.titleColor()).withBold(true)),
                  buildSectionLabel(theme.trackLabel(), theme),
                  buildSectionValue(buildTrackBarComponent(snapshot.completedTracks(), 4, theme), theme),
                  buildSectionLabel(Component.translatable("tooltip.somakespells.progress.label.progress"), theme),
                  buildSectionValue(Component.literal(progressText).withStyle(Style.EMPTY.withColor(theme.titleColor())), theme),
                  Component.empty(),
                  buildSectionLabel(Component.translatable("tooltip.somakespells.progress.label.objective"), theme),
                  buildBulletLine(describeObjective(snapshot.completedTracks(), valuesFor(book), book), theme)
               )
            );
         }
      }
   }

   private static Component describeObjective(int stage, SpellbookProgressionConfig.TierProgressionValues values, AbstractElementalProgressSpellBook book) {
      return (Component)(switch (stage) {
         case 0 -> Component.translatable("tooltip.somakespells.progress.objective.cast", new Object[]{values.castsRequired(), getSchoolDisplayName(book)});
         case 1 -> describeConditionalObjective(values, book);
         case 2 -> Component.translatable(
            "tooltip.somakespells.progress.objective.third", new Object[]{values.targetMobKillsRequired(), getMobDisplayName(values.targetMobId())}
         );
         case 3 -> Component.translatable(
            "tooltip.somakespells.progress.objective.fourth", new Object[]{values.finalMobKillsRequired(), getMobDisplayName(values.finalMobId())}
         );
         default -> Component.translatable("tooltip.somakespells.progress.objective.complete");
      });
   }

   private static Component describeConditionalObjective(SpellbookProgressionConfig.TierProgressionValues values, AbstractElementalProgressSpellBook book) {
      int amount = values.conditionalKillsRequired();

      return switch (book.getConditionalObjectiveKind()) {
         case ANY -> Component.translatable("tooltip.somakespells.progress.objective.conditional.any", new Object[]{amount});
         case BURNING -> Component.translatable("tooltip.somakespells.progress.objective.conditional.burning", new Object[]{amount});
         case WET -> Component.translatable("tooltip.somakespells.progress.objective.conditional.wet", new Object[]{amount});
         case UNDEAD -> Component.translatable("tooltip.somakespells.progress.objective.conditional.undead", new Object[]{amount});
         case ENDER -> Component.translatable("tooltip.somakespells.progress.objective.conditional.ender", new Object[]{amount});
         case ILLAGER -> Component.translatable("tooltip.somakespells.progress.objective.conditional.illager", new Object[]{amount});
         case ELECTROCUTED -> Component.translatable("tooltip.somakespells.progress.objective.conditional.electrocuted", new Object[]{amount});
         case FROZEN -> Component.translatable("tooltip.somakespells.progress.objective.conditional.frozen", new Object[]{amount});
         case POISONED -> Component.translatable("tooltip.somakespells.progress.objective.conditional.poisoned", new Object[]{amount});
      };
   }

   private static Component getSchoolDisplayName(AbstractElementalProgressSpellBook book) {
      SchoolType schoolType = SchoolRegistry.getSchool(book.getTrackedSchoolId());
      return schoolType != null
         ? schoolType.getDisplayName().copy()
         : Component.translatable("school." + book.getTrackedSchoolId().getNamespace() + "." + book.getTrackedSchoolId().getPath());
   }

   private static Component getMobDisplayName(String entityId) {
      ResourceLocation mobId;
      try {
         mobId = ResourceLocation.parse(entityId);
      } catch (Exception var3) {
         return Component.literal(entityId);
      }

      if (BuiltInRegistries.ENTITY_TYPE.containsKey(mobId)) {
         EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(mobId);
         return Component.translatable(entityType.getDescriptionId());
      } else {
         return Component.literal(mobId.getPath().replace('_', ' '));
      }
   }

   public static SpellbookProgressHelper.ProgressSnapshot readProgress(ItemStack stack, AbstractElementalProgressSpellBook book) {
      if (book != null && stack != null && !stack.isEmpty() && hasFamilyConfig(book)) {
         if (book.getProgressionTier() >= 3) {
            return new SpellbookProgressHelper.ProgressSnapshot(4, 0, 0, true);
         } else {
            CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            CompoundTag progressTag = getProgressTag(tag, book);
            int stage = Mth.clamp(progressTag.getInt("stage"), 0, 4);
            boolean maxed = stage >= 4;
            int requiredXp = maxed ? 0 : getRequiredXp(book, stage);
            int currentXp = maxed ? 0 : Mth.clamp(progressTag.getInt("progress"), 0, requiredXp);
            return new SpellbookProgressHelper.ProgressSnapshot(stage, currentXp, requiredXp, maxed);
         }
      } else {
         return new SpellbookProgressHelper.ProgressSnapshot(0, 0, 0, false);
      }
   }

   private static int currentStage(ItemStack stack, AbstractElementalProgressSpellBook book) {
      return readProgress(stack, book).completedTracks();
   }

   private static SpellbookProgressionConfig.TierProgressionValues valuesFor(AbstractElementalProgressSpellBook book) {
      SpellbookProgressionConfig.FamilyProgressionConfig family = SpellbookProgressionConfig.getFamily(book.getProgressionFamilyId());
      if (family == null) {
         throw new IllegalStateException("Missing spellbook progression config for family " + book.getProgressionFamilyId());
      } else {
         return family.tier(book.getProgressionTier());
      }
   }

   private static int getRequiredXp(AbstractElementalProgressSpellBook book, int stage) {
      SpellbookProgressionConfig.TierProgressionValues values = valuesFor(book);

      return switch (stage) {
         case 0 -> values.castsRequired();
         case 1 -> values.conditionalKillsRequired();
         case 2 -> values.targetMobKillsRequired();
         case 3 -> values.finalMobKillsRequired();
         default -> 0;
      };
   }

   private static void incrementObjectiveProgress(ItemStack stack, AbstractElementalProgressSpellBook book, int amount) {
      if (!stack.isEmpty() && amount > 0) {
         CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            CompoundTag progressTag = getOrCreateProgressTag(tag, book);
            int stage = Mth.clamp(progressTag.getInt("stage"), 0, 4);
            if (stage < 4) {
               int required = getRequiredXp(book, stage);
               int currentProgress = Math.max(0, progressTag.getInt("progress"));
               currentProgress = Math.min(required, currentProgress + amount);
               if (currentProgress >= required) {
                  stage++;
                  currentProgress = 0;
               }

               progressTag.putString("family", book.getProgressionFamilyId());
               progressTag.putInt("tier", book.getProgressionTier());
               progressTag.putInt("stage", stage);
               progressTag.putInt("progress", currentProgress);
               tag.put("somake_spellbook_progress", progressTag);
            }
         });
      }
   }

   private static boolean matchesConditionalObjective(AbstractElementalProgressSpellBook book, LivingEntity target) {
      if (book != null && target != null) {
         return switch (book.getConditionalObjectiveKind()) {
            case ANY -> true;
            case BURNING -> target.getRemainingFireTicks() > 0;
            case WET -> target.isInWaterRainOrBubble() || hasEffect(target, WET_EFFECT_ID);
            case UNDEAD -> target.getType().is(EntityTypeTags.UNDEAD);
            case ENDER -> isEnderTarget(target);
            case ILLAGER -> target.getType().is(EntityTypeTags.RAIDERS);
            case ELECTROCUTED -> hasEffect(target, ELECTROCUTED_EFFECT_ID);
            case FROZEN -> target.getTicksFrozen() > 0;
            case POISONED -> target.hasEffect(MobEffects.POISON) || hasEffect(target, OVERGROWN_EFFECT_ID);
         };
      } else {
         return false;
      }
   }

   private static boolean matchesMobId(LivingEntity target, String entityId) {
      if (target != null && entityId != null && !entityId.isBlank()) {
         ResourceLocation targetId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());

         ResourceLocation configuredId;
         try {
            configuredId = ResourceLocation.parse(entityId);
         } catch (Exception var5) {
            return false;
         }

         return targetId.equals(configuredId);
      } else {
         return false;
      }
   }

   private static boolean hasEffect(LivingEntity target, ResourceLocation effectId) {
      return target.getActiveEffects()
         .stream()
         .anyMatch(effect -> effect.getEffect() != null && effectId.equals(BuiltInRegistries.MOB_EFFECT.getKey((MobEffect)effect.getEffect().value())));
   }

   private static boolean isEnderTarget(LivingEntity target) {
      EntityType<?> type = target.getType();
      return type == EntityType.ENDERMAN || type == EntityType.ENDERMITE || type == EntityType.SHULKER || type == EntityType.ENDER_DRAGON;
   }

   private static Component buildSectionLabel(Component label, SpellbookProgressHelper.ProgressTheme theme) {
      return Component.literal("| ")
         .withStyle(Style.EMPTY.withColor(theme.titleColor()))
         .append(label.copy().withStyle(Style.EMPTY.withColor(theme.accentColor()).withBold(true)));
   }

   private static Component buildSectionValue(Component value, SpellbookProgressHelper.ProgressTheme theme) {
      return Component.literal("| - ").withStyle(Style.EMPTY.withColor(theme.titleColor())).append(value.copy());
   }

   private static Component buildBulletLine(Component value, SpellbookProgressHelper.ProgressTheme theme) {
      return Component.literal("- ")
         .withStyle(Style.EMPTY.withColor(theme.titleColor()))
         .append(value.copy().withStyle(Style.EMPTY.withColor(theme.titleColor()).withItalic(true)));
   }

   private static MutableComponent buildTrackBarComponent(int completedTracks, int maxTracks, SpellbookProgressHelper.ProgressTheme theme) {
      MutableComponent bar = Component.empty();

      for (int i = 0; i < maxTracks; i++) {
         boolean filled = i < completedTracks;
         bar.append(Component.literal("█").withStyle(Style.EMPTY.withColor(filled ? theme.titleColor() : theme.emptyBarColor())));
      }

      return bar;
   }

   private static CompoundTag getProgressTag(CompoundTag tag, AbstractElementalProgressSpellBook book) {
      if (!(tag.contains("somake_spellbook_progress") && tag.get("somake_spellbook_progress") instanceof CompoundTag progressTag)) {
         return new CompoundTag();
      } else {
         return book.getProgressionFamilyId().equals(progressTag.getString("family")) && book.getProgressionTier() == progressTag.getInt("tier")
            ? progressTag.copy()
            : new CompoundTag();
      }
   }

   private static CompoundTag getOrCreateProgressTag(CompoundTag tag, AbstractElementalProgressSpellBook book) {
      CompoundTag progressTag = getProgressTag(tag, book);
      progressTag.putString("family", book.getProgressionFamilyId());
      progressTag.putInt("tier", book.getProgressionTier());
      return progressTag;
   }

   private static boolean hasFamilyConfig(AbstractElementalProgressSpellBook book) {
      return book != null && SpellbookProgressionConfig.getFamily(book.getProgressionFamilyId()) != null;
   }

   public record ProgressSnapshot(int completedTracks, int currentXp, int requiredXp, boolean maxed) {
   }

   public record ProgressTheme(
      Component title,
      Component trackLabel,
      List<Component> objectives,
      int titleColor,
      int accentColor,
      int emptyBarColor,
      int highlightColor,
      int flavorColor
   ) {
   }
}
