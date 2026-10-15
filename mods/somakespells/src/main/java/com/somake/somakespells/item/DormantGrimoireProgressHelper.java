package com.somake.somakespells.item;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class DormantGrimoireProgressHelper {
   private static final String ROOT_KEY = "somake_dormant_grimoire_progress";
   private static final String LOCKED_PATH_KEY = "locked_path";
   private static final String BLOOD_STAGE_KEY = "blood_stage";
   private static final String BLOOD_PROGRESS_KEY = "blood_progress";
   private static final String HOLY_STAGE_KEY = "holy_stage";
   private static final String HOLY_PROGRESS_KEY = "holy_progress";
   private static final int OBJECTIVES_PER_PATH = 4;
   private static final double ZOMBIE_VILLAGER_RADIUS = 12.0;
   private static final int BLOOD_PASSIVE_KILLS_REQUIRED = 24;
   private static final int BLOOD_VILLAGERS_REQUIRED = 6;
   private static final int BLOOD_CASTS_REQUIRED = 32;
   private static final int BLOOD_TRADERS_REQUIRED = 2;
   private static final int HOLY_HOSTILE_KILLS_REQUIRED = 32;
   private static final int HOLY_ZOMBIES_NEAR_VILLAGERS_REQUIRED = 8;
   private static final int HOLY_CASTS_REQUIRED = 32;
   private static final int HOLY_ILLAGERS_REQUIRED = 12;
   private static final int BLOOD_TITLE_COLOR = 15833497;
   private static final int BLOOD_ACCENT_COLOR = 12139580;
   private static final int BLOOD_EMPTY_COLOR = 5906207;
   private static final int HOLY_TITLE_COLOR = 15780202;
   private static final int HOLY_ACCENT_COLOR = 13081138;
   private static final int HOLY_EMPTY_COLOR = 7232551;
   private static final int DORMANT_TITLE_COLOR = 14143934;

   private DormantGrimoireProgressHelper() {
   }

   public static int dormantTitleColor() {
      return 14143934;
   }

   public static int holyTitleColor() {
      return 15780202;
   }

   public static int bloodTitleColor() {
      return 15833497;
   }

   public static ItemStack getHeldDormantGrimoire(Player player) {
      if (player == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack mainHand = player.getMainHandItem();
         if (mainHand.getItem() instanceof DormantGrimoireItem) {
            return mainHand;
         } else {
            ItemStack offHand = player.getOffhandItem();
            return offHand.getItem() instanceof DormantGrimoireItem ? offHand : ItemStack.EMPTY;
         }
      }
   }

   public static void recordKill(ItemStack stack, LivingEntity target) {
      if (isDormantGrimoire(stack) && target != null && !isLocked(stack)) {
         for (DormantGrimoireProgressHelper.Path path : DormantGrimoireProgressHelper.Path.values()) {
            DormantGrimoireProgressHelper.Objective objective = currentObjective(stack, path);
            if (objective != null && matchesKillObjective(objective, target)) {
               incrementObjectiveProgress(stack, path, 1);
            }
         }
      }
   }

   public static void recordSpellCast(ItemStack stack, ResourceLocation schoolId) {
      if (isDormantGrimoire(stack) && schoolId != null && !isLocked(stack)) {
         for (DormantGrimoireProgressHelper.Path path : DormantGrimoireProgressHelper.Path.values()) {
            DormantGrimoireProgressHelper.Objective objective = currentObjective(stack, path);
            if (objective != null && matchesCastObjective(objective, schoolId)) {
               incrementObjectiveProgress(stack, path, 1);
            }
         }
      }
   }

   public static boolean hasAlignment(ItemStack stack, String requiredPathId) {
      DormantGrimoireProgressHelper.Path requiredPath = DormantGrimoireProgressHelper.Path.fromId(requiredPathId).orElse(null);
      return requiredPath == null ? false : lockedPath(stack).map(path -> path == requiredPath).orElse(false);
   }

   public static boolean isAlignedToHoly(ItemStack stack) {
      return hasAlignment(stack, DormantGrimoireProgressHelper.Path.HOLY.id());
   }

   public static boolean isAlignedToBlood(ItemStack stack) {
      return hasAlignment(stack, DormantGrimoireProgressHelper.Path.BLOOD.id());
   }

   public static boolean isLocked(ItemStack stack) {
      return lockedPath(stack).isPresent();
   }

   public static void resetProgress(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove("somake_dormant_grimoire_progress"));
      }
   }

   public static ItemStack createAlignmentPreview(ItemStack stack, String pathId) {
      DormantGrimoireProgressHelper.Path path = DormantGrimoireProgressHelper.Path.fromId(pathId).orElse(null);
      if (stack != null && !stack.isEmpty() && path != null) {
         ItemStack preview = stack.copy();
         CustomData.update(DataComponents.CUSTOM_DATA, preview, tag -> {
            CompoundTag progressTag = new CompoundTag();
            progressTag.putString("locked_path", path.id());
            progressTag.putInt("blood_stage", path == DormantGrimoireProgressHelper.Path.BLOOD ? 4 : 0);
            progressTag.putInt("blood_progress", 0);
            progressTag.putInt("holy_stage", path == DormantGrimoireProgressHelper.Path.HOLY ? 4 : 0);
            progressTag.putInt("holy_progress", 0);
            tag.put("somake_dormant_grimoire_progress", progressTag);
         });
         return preview;
      } else {
         return stack == null ? ItemStack.EMPTY : stack.copy();
      }
   }

   public static void appendTooltip(ItemStack stack, List<Component> lines) {
      if (isDormantGrimoire(stack) && lines != null) {
         DormantGrimoireProgressHelper.ProgressSnapshot blood = snapshot(stack, DormantGrimoireProgressHelper.Path.BLOOD);
         DormantGrimoireProgressHelper.ProgressSnapshot holy = snapshot(stack, DormantGrimoireProgressHelper.Path.HOLY);
         Optional<DormantGrimoireProgressHelper.Path> lockedPath = lockedPath(stack);
         lines.add(Component.empty());
         lines.add(Component.translatable("tooltip.somakespells.dormant_grimoire.title").withStyle(Style.EMPTY.withColor(14143934).withBold(true)));
         if (lockedPath.isPresent()) {
            MutableComponent alignmentLine = Component.empty();
            alignmentLine.append(buildSectionLabel(Component.translatable("tooltip.somakespells.dormant_grimoire.alignment.label"), 14143934));
            alignmentLine.append(
               Component.translatable(lockedPath.get().nameKey())
                  .withStyle(Style.EMPTY.withColor(lockedPath.get() == DormantGrimoireProgressHelper.Path.HOLY ? 15780202 : 15833497).withBold(true))
            );
            lines.add(alignmentLine);
         }

         appendPathTooltip(lines, DormantGrimoireProgressHelper.Path.HOLY, holy, lockedPath.orElse(null));
         appendPathTooltip(lines, DormantGrimoireProgressHelper.Path.BLOOD, blood, lockedPath.orElse(null));
      }
   }

   private static void appendPathTooltip(
      List<Component> lines,
      DormantGrimoireProgressHelper.Path path,
      DormantGrimoireProgressHelper.ProgressSnapshot snapshot,
      DormantGrimoireProgressHelper.Path lockedPath
   ) {
      int titleColor = path == DormantGrimoireProgressHelper.Path.HOLY ? 15780202 : 15833497;
      int accentColor = path == DormantGrimoireProgressHelper.Path.HOLY ? 13081138 : 12139580;
      int emptyColor = path == DormantGrimoireProgressHelper.Path.HOLY ? 7232551 : 5906207;
      lines.add(buildSectionLabel(Component.translatable(path.nameKey()), titleColor));
      lines.add(buildSectionValue(buildTrackBarComponent(snapshot.completedObjectives(), 4, titleColor, emptyColor), accentColor));
      String progressText = snapshot.complete() ? "MAX" : snapshot.currentProgress() + " / " + snapshot.requiredProgress();
      lines.add(
         buildSectionValue(
            Component.translatable("tooltip.somakespells.dormant_grimoire.progress", new Object[]{progressText}).withStyle(Style.EMPTY.withColor(titleColor)),
            accentColor
         )
      );
      Component objectiveText;
      if (snapshot.complete()) {
         objectiveText = Component.translatable("tooltip.somakespells.dormant_grimoire.objective.complete");
      } else if (lockedPath != null && lockedPath != path) {
         objectiveText = Component.translatable("tooltip.somakespells.dormant_grimoire.objective.locked");
      } else {
         objectiveText = describeObjective(path, snapshot.completedObjectives());
      }

      lines.add(buildSectionValue(objectiveText.copy().withStyle(Style.EMPTY.withColor(titleColor).withItalic(true)), accentColor));
   }

   private static Component describeObjective(DormantGrimoireProgressHelper.Path path, int stage) {
      DormantGrimoireProgressHelper.Objective objective = objectiveFor(path, stage);
      if (objective == null) {
         return Component.translatable("tooltip.somakespells.dormant_grimoire.objective.complete");
      } else {
         return switch (objective) {
            case BLOOD_KILL_PASSIVE -> Component.translatable(
               "tooltip.somakespells.dormant_grimoire.objective.blood.passive", new Object[]{objective.requiredProgress()}
            );
            case BLOOD_KILL_VILLAGER -> Component.translatable(
               "tooltip.somakespells.dormant_grimoire.objective.blood.villager", new Object[]{objective.requiredProgress()}
            );
            case BLOOD_CAST -> Component.translatable("tooltip.somakespells.dormant_grimoire.objective.blood.cast", new Object[]{objective.requiredProgress()});
            case BLOOD_KILL_TRADER -> Component.translatable(
               "tooltip.somakespells.dormant_grimoire.objective.blood.trader", new Object[]{objective.requiredProgress()}
            );
            case HOLY_KILL_HOSTILE -> Component.translatable(
               "tooltip.somakespells.dormant_grimoire.objective.holy.hostile", new Object[]{objective.requiredProgress()}
            );
            case HOLY_KILL_ZOMBIE_NEAR_VILLAGER -> Component.translatable(
               "tooltip.somakespells.dormant_grimoire.objective.holy.zombie_villager", new Object[]{objective.requiredProgress()}
            );
            case HOLY_CAST -> Component.translatable("tooltip.somakespells.dormant_grimoire.objective.holy.cast", new Object[]{objective.requiredProgress()});
            case HOLY_KILL_ILLAGER -> Component.translatable(
               "tooltip.somakespells.dormant_grimoire.objective.holy.illager", new Object[]{objective.requiredProgress()}
            );
         };
      }
   }

   private static boolean matchesKillObjective(DormantGrimoireProgressHelper.Objective objective, LivingEntity target) {
      return switch (objective) {
         case BLOOD_KILL_PASSIVE -> target instanceof Animal && !(target instanceof Enemy);
         case BLOOD_KILL_VILLAGER -> target instanceof Villager;
         default -> false;
         case BLOOD_KILL_TRADER -> target instanceof WanderingTrader;
         case HOLY_KILL_HOSTILE -> target instanceof Enemy;
         case HOLY_KILL_ZOMBIE_NEAR_VILLAGER -> target instanceof Zombie
            && !target.level().getEntitiesOfClass(Villager.class, target.getBoundingBox().inflate(12.0), LivingEntity::isAlive).isEmpty();
         case HOLY_KILL_ILLAGER -> target.getType().is(EntityTypeTags.RAIDERS);
      };
   }

   private static boolean matchesCastObjective(DormantGrimoireProgressHelper.Objective objective, ResourceLocation schoolId) {
      return switch (objective) {
         case BLOOD_CAST -> SchoolRegistry.BLOOD_RESOURCE.equals(schoolId);
         case HOLY_CAST -> SchoolRegistry.HOLY_RESOURCE.equals(schoolId);
         default -> false;
      };
   }

   private static void incrementObjectiveProgress(ItemStack stack, DormantGrimoireProgressHelper.Path path, int amount) {
      if (!stack.isEmpty() && amount > 0) {
         CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            CompoundTag progressTag = getOrCreateProgressTag(tag);
            if (!readLockedPath(progressTag).isPresent()) {
               int currentStage = getStage(progressTag, path);
               if (currentStage < 4) {
                  DormantGrimoireProgressHelper.Objective objective = objectiveFor(path, currentStage);
                  if (objective != null) {
                     int required = objective.requiredProgress();
                     int currentProgress = Mth.clamp(getProgress(progressTag, path), 0, required);
                     currentProgress = Math.min(required, currentProgress + amount);
                     if (currentProgress >= required) {
                        currentStage++;
                        currentProgress = 0;
                     }

                     setStage(progressTag, path, currentStage);
                     setProgress(progressTag, path, currentProgress);
                     if (currentStage >= 4) {
                        progressTag.putString("locked_path", path.id());
                     }

                     tag.put("somake_dormant_grimoire_progress", progressTag);
                  }
               }
            }
         });
      }
   }

   private static DormantGrimoireProgressHelper.Objective currentObjective(ItemStack stack, DormantGrimoireProgressHelper.Path path) {
      return objectiveFor(path, snapshot(stack, path).completedObjectives());
   }

   private static DormantGrimoireProgressHelper.Objective objectiveFor(DormantGrimoireProgressHelper.Path path, int stage) {
      return switch (path) {
         case HOLY -> {
            switch (stage) {
               case 0:
                  yield DormantGrimoireProgressHelper.Objective.HOLY_KILL_HOSTILE;
               case 1:
                  yield DormantGrimoireProgressHelper.Objective.HOLY_KILL_ZOMBIE_NEAR_VILLAGER;
               case 2:
                  yield DormantGrimoireProgressHelper.Objective.HOLY_CAST;
               case 3:
                  yield DormantGrimoireProgressHelper.Objective.HOLY_KILL_ILLAGER;
               default:
                  yield null;
            }
         }
         case BLOOD -> {
            switch (stage) {
               case 0:
                  yield DormantGrimoireProgressHelper.Objective.BLOOD_KILL_PASSIVE;
               case 1:
                  yield DormantGrimoireProgressHelper.Objective.BLOOD_KILL_VILLAGER;
               case 2:
                  yield DormantGrimoireProgressHelper.Objective.BLOOD_CAST;
               case 3:
                  yield DormantGrimoireProgressHelper.Objective.BLOOD_KILL_TRADER;
               default:
                  yield null;
            }
         }
      };
   }

   private static DormantGrimoireProgressHelper.ProgressSnapshot snapshot(ItemStack stack, DormantGrimoireProgressHelper.Path path) {
      if (!isDormantGrimoire(stack)) {
         return new DormantGrimoireProgressHelper.ProgressSnapshot(0, 0, objectiveFor(path, 0).requiredProgress(), false);
      } else {
         CompoundTag progressTag = getProgressTag(stack);
         int stage = Mth.clamp(getStage(progressTag, path), 0, 4);
         boolean complete = stage >= 4;
         int required = complete ? 0 : objectiveFor(path, stage).requiredProgress();
         int current = complete ? 0 : Mth.clamp(getProgress(progressTag, path), 0, required);
         return new DormantGrimoireProgressHelper.ProgressSnapshot(stage, current, required, complete);
      }
   }

   private static Optional<DormantGrimoireProgressHelper.Path> lockedPath(ItemStack stack) {
      return !isDormantGrimoire(stack) ? Optional.empty() : readLockedPath(getProgressTag(stack));
   }

   private static Optional<DormantGrimoireProgressHelper.Path> readLockedPath(CompoundTag progressTag) {
      return progressTag != null && progressTag.contains("locked_path")
         ? DormantGrimoireProgressHelper.Path.fromId(progressTag.getString("locked_path"))
         : Optional.empty();
   }

   private static CompoundTag getProgressTag(ItemStack stack) {
      CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      return tag.contains("somake_dormant_grimoire_progress") && tag.get("somake_dormant_grimoire_progress") instanceof CompoundTag progressTag
         ? progressTag.copy()
         : new CompoundTag();
   }

   private static CompoundTag getOrCreateProgressTag(CompoundTag tag) {
      return tag.contains("somake_dormant_grimoire_progress") && tag.get("somake_dormant_grimoire_progress") instanceof CompoundTag progressTag
         ? progressTag.copy()
         : new CompoundTag();
   }

   private static int getStage(CompoundTag progressTag, DormantGrimoireProgressHelper.Path path) {
      return progressTag.getInt(path == DormantGrimoireProgressHelper.Path.BLOOD ? "blood_stage" : "holy_stage");
   }

   private static int getProgress(CompoundTag progressTag, DormantGrimoireProgressHelper.Path path) {
      return progressTag.getInt(path == DormantGrimoireProgressHelper.Path.BLOOD ? "blood_progress" : "holy_progress");
   }

   private static void setStage(CompoundTag progressTag, DormantGrimoireProgressHelper.Path path, int stage) {
      progressTag.putInt(path == DormantGrimoireProgressHelper.Path.BLOOD ? "blood_stage" : "holy_stage", stage);
   }

   private static void setProgress(CompoundTag progressTag, DormantGrimoireProgressHelper.Path path, int progress) {
      progressTag.putInt(path == DormantGrimoireProgressHelper.Path.BLOOD ? "blood_progress" : "holy_progress", progress);
   }

   private static boolean isDormantGrimoire(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.getItem() instanceof DormantGrimoireItem;
   }

   private static Component buildSectionLabel(Component label, int titleColor) {
      return Component.literal("| ")
         .withStyle(Style.EMPTY.withColor(titleColor))
         .append(label.copy().withStyle(Style.EMPTY.withColor(titleColor).withBold(true)));
   }

   private static Component buildSectionValue(Component value, int accentColor) {
      return Component.literal("| - ").withStyle(Style.EMPTY.withColor(accentColor)).append(value.copy());
   }

   private static MutableComponent buildTrackBarComponent(int completedTracks, int maxTracks, int filledColor, int emptyColor) {
      MutableComponent bar = Component.empty();

      for (int i = 0; i < maxTracks; i++) {
         boolean filled = i < completedTracks;
         bar.append(Component.literal("█").withStyle(Style.EMPTY.withColor(filled ? filledColor : emptyColor)));
      }

      return bar;
   }

   private static enum Objective {
      BLOOD_KILL_PASSIVE(24),
      BLOOD_KILL_VILLAGER(6),
      BLOOD_CAST(32),
      BLOOD_KILL_TRADER(2),
      HOLY_KILL_HOSTILE(32),
      HOLY_KILL_ZOMBIE_NEAR_VILLAGER(8),
      HOLY_CAST(32),
      HOLY_KILL_ILLAGER(12);

      private final int requiredProgress;

      private Objective(int requiredProgress) {
         this.requiredProgress = requiredProgress;
      }

      private int requiredProgress() {
         return this.requiredProgress;
      }
   }

   public static enum Path {
      HOLY("holy", "tooltip.somakespells.dormant_grimoire.path.holy"),
      BLOOD("blood", "tooltip.somakespells.dormant_grimoire.path.blood");

      private final String id;
      private final String nameKey;

      private Path(String id, String nameKey) {
         this.id = id;
         this.nameKey = nameKey;
      }

      public String id() {
         return this.id;
      }

      public String nameKey() {
         return this.nameKey;
      }

      public static Optional<DormantGrimoireProgressHelper.Path> fromId(String id) {
         if (id != null && !id.isBlank()) {
            for (DormantGrimoireProgressHelper.Path path : values()) {
               if (path.id.equals(id)) {
                  return Optional.of(path);
               }
            }

            return Optional.empty();
         } else {
            return Optional.empty();
         }
      }
   }

   private record ProgressSnapshot(int completedObjectives, int currentProgress, int requiredProgress, boolean complete) {
   }
}
