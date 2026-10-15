package com.chadate.spellelemental.element.reaction.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class ElementReactionRegistry {
   private static final Set<String> DAMAGE_REACTIONS = new LinkedHashSet<>();
   private static final Set<String> TICK_REACTIONS = new LinkedHashSet<>();
   private static final Map<String, String> DAMAGE_COMBO_INDEX = new HashMap<>();
   private static final Map<String, List<ElementReactionRegistry.ReactionEffect>> REACTION_EFFECTS = new HashMap<>();
   private static final Map<String, List<ElementReactionRegistry.ReactionEffect>> DIRECTIONAL_EFFECTS = new HashMap<>();
   private static final Map<String, List<ElementReactionRegistry.AttributeEffect>> ATTRIBUTE_EFFECTS = new HashMap<>();
   private static final Map<String, List<ElementReactionRegistry.AttributeEffect>> DIRECTIONAL_ATTRIBUTE_EFFECTS = new HashMap<>();
   private static final Map<String, List<ElementReactionRegistry.TickRule>> TICK_RULES = new HashMap<>();
   private static final Map<String, Boolean> REACTION_CONSUME_FLAG = new HashMap<>();
   private static final Map<String, Double> DAMAGE_CONSUME_RATIO = new HashMap<>();

   private ElementReactionRegistry() {
   }

   public static void clear() {
      DAMAGE_REACTIONS.clear();
      TICK_REACTIONS.clear();
      DAMAGE_COMBO_INDEX.clear();
      REACTION_EFFECTS.clear();
      DIRECTIONAL_EFFECTS.clear();
      ATTRIBUTE_EFFECTS.clear();
      TICK_RULES.clear();
      REACTION_CONSUME_FLAG.clear();
      DAMAGE_CONSUME_RATIO.clear();
   }

   public static void add(String reactionId, String triggerType) {
      if (reactionId != null && !reactionId.isBlank()) {
         String t = triggerType == null ? "damage" : triggerType.trim().toLowerCase();
         if ("tick".equals(t)) {
            TICK_REACTIONS.add(reactionId);
         } else {
            DAMAGE_REACTIONS.add(reactionId);
         }

         if (!"tick".equals(t)) {
            REACTION_CONSUME_FLAG.put(reactionId, Boolean.TRUE);
         }
      }
   }

   public static Set<String> getDamageReactions() {
      return Collections.unmodifiableSet(DAMAGE_REACTIONS);
   }

   public static Set<String> getTickReactions() {
      return Collections.unmodifiableSet(TICK_REACTIONS);
   }

   public static void setConsumeFlag(String reactionId, boolean consume) {
      if (reactionId != null && !reactionId.isBlank()) {
         REACTION_CONSUME_FLAG.put(reactionId, consume);
      }
   }

   public static boolean shouldConsumeElements(String reactionId) {
      Boolean v = REACTION_CONSUME_FLAG.get(reactionId);
      return v == null || v;
   }

   public static void setConsumeRatio(String source, String target, double ratio) {
      if (source != null && !source.isBlank() && target != null && !target.isBlank()) {
         String key = makeOrderedKey(safeElem(source), safeElem(target));
         DAMAGE_CONSUME_RATIO.put(key, Math.max(0.0, ratio));
      }
   }

   public static double getConsumeRatio(String source, String target) {
      if (source != null && !source.isBlank() && target != null && !target.isBlank()) {
         String key = makeOrderedKey(safeElem(source), safeElem(target));
         Double ratio = DAMAGE_CONSUME_RATIO.get(key);
         return ratio == null ? 1.0 : ratio;
      } else {
         return 1.0;
      }
   }

   public static void setTickRule(
      String reactionId, Map<String, Integer> requirements, Map<String, Integer> consume, List<ElementReactionRegistry.ReactionEffect> effects, int interval
   ) {
      if (reactionId != null && !reactionId.isBlank()) {
         Map<String, Integer> req = normalizeElemIntMap(requirements);
         Map<String, Integer> con = normalizeElemIntMap(consume);
         List<ElementReactionRegistry.ReactionEffect> effs = (List<ElementReactionRegistry.ReactionEffect>)(effects == null
            ? Collections.emptyList()
            : new ArrayList<>(effects));
         int iv = Math.max(1, interval);
         TICK_RULES.computeIfAbsent(reactionId, k -> new ArrayList<>()).add(new ElementReactionRegistry.TickRule(req, con, effs, iv));
      }
   }

   public static List<ElementReactionRegistry.TickRule> getTickRules(String reactionId) {
      List<ElementReactionRegistry.TickRule> list = TICK_RULES.get(reactionId);
      return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
   }

   private static Map<String, Integer> normalizeElemIntMap(Map<String, Integer> in) {
      Map<String, Integer> out = new HashMap<>();
      if (in != null) {
         for (Entry<String, Integer> e : in.entrySet()) {
            String k = safeElem(e.getKey());
            if (!k.isEmpty()) {
               out.put(k, Math.max(0, e.getValue() == null ? 0 : e.getValue()));
            }
         }
      }

      return out;
   }

   public static void indexDamageCombination(String reactionId, List<String> participants, boolean commutative) {
      if (reactionId != null && !reactionId.isBlank() && participants != null && participants.size() >= 2) {
         String a = safeElem(participants.get(0));
         String b = safeElem(participants.get(1));
         if (!a.isEmpty() && !b.isEmpty()) {
            if (commutative) {
               String key = makeUnorderedKey(a, b);
               DAMAGE_COMBO_INDEX.put(key, reactionId);
            } else {
               String key = makeOrderedKey(a, b);
               DAMAGE_COMBO_INDEX.put(key, reactionId);
            }
         }
      }
   }

   public static void indexDamageOrdered(String reactionId, String source, String target) {
      if (reactionId != null && !reactionId.isBlank()) {
         String a = safeElem(source);
         String b = safeElem(target);
         if (!a.isEmpty() && !b.isEmpty()) {
            String key = makeOrderedKey(a, b);
            DAMAGE_COMBO_INDEX.put(key, reactionId);
         }
      }
   }

   public static String findDamageReactionId(String source, String target) {
      String a = safeElem(source);
      String b = safeElem(target);
      if (!a.isEmpty() && !b.isEmpty()) {
         String id = DAMAGE_COMBO_INDEX.get(makeOrderedKey(a, b));
         return id != null ? id : DAMAGE_COMBO_INDEX.get(makeUnorderedKey(a, b));
      } else {
         return null;
      }
   }

   public static boolean hasAnyDamageCombos() {
      return !DAMAGE_COMBO_INDEX.isEmpty();
   }

   private static String makeUnorderedKey(String a, String b) {
      String x = a.compareTo(b) <= 0 ? a : b;
      String y = a.compareTo(b) <= 0 ? b : a;
      return x + "|" + y;
   }

   private static String makeOrderedKey(String a, String b) {
      return a + "->" + b;
   }

   private static String safeElem(String s) {
      return s == null ? "" : s.trim().toLowerCase();
   }

   public static void addEffect(String reactionId, ElementReactionRegistry.ReactionEffect effect) {
      if (reactionId != null && !reactionId.isBlank() && effect != null) {
         REACTION_EFFECTS.computeIfAbsent(reactionId, k -> new ArrayList<>()).add(effect);
      }
   }

   public static List<ElementReactionRegistry.ReactionEffect> getEffects(String reactionId) {
      List<ElementReactionRegistry.ReactionEffect> list = REACTION_EFFECTS.get(reactionId);
      return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
   }

   public static void addAttributeEffect(String reactionId, ElementReactionRegistry.AttributeEffect effect) {
      if (reactionId != null && !reactionId.isBlank() && effect != null) {
         ATTRIBUTE_EFFECTS.computeIfAbsent(reactionId, k -> new ArrayList<>()).add(effect);
      }
   }

   public static List<ElementReactionRegistry.AttributeEffect> getAttributeEffects(String reactionId) {
      List<ElementReactionRegistry.AttributeEffect> list = ATTRIBUTE_EFFECTS.get(reactionId);
      return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
   }

   public static void addDirectionalAttributeEffect(String source, String target, ElementReactionRegistry.AttributeEffect effect) {
      String a = safeElem(source);
      String b = safeElem(target);
      if (!a.isEmpty() && !b.isEmpty() && effect != null) {
         String key = makeOrderedKey(a, b);
         DIRECTIONAL_ATTRIBUTE_EFFECTS.computeIfAbsent(key, k -> new ArrayList<>()).add(effect);
      }
   }

   public static List<ElementReactionRegistry.AttributeEffect> getDirectionalAttributeEffects(String source, String target) {
      String a = safeElem(source);
      String b = safeElem(target);
      if (!a.isEmpty() && !b.isEmpty()) {
         List<ElementReactionRegistry.AttributeEffect> list = DIRECTIONAL_ATTRIBUTE_EFFECTS.get(makeOrderedKey(a, b));
         return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
      } else {
         return Collections.emptyList();
      }
   }

   public static void addDirectionalEffect(String source, String target, ElementReactionRegistry.ReactionEffect effect) {
      String a = safeElem(source);
      String b = safeElem(target);
      if (!a.isEmpty() && !b.isEmpty() && effect != null) {
         String key = makeOrderedKey(a, b);
         DIRECTIONAL_EFFECTS.computeIfAbsent(key, k -> new ArrayList<>()).add(effect);
      }
   }

   public static List<ElementReactionRegistry.ReactionEffect> getDirectionalEffects(String source, String target) {
      String a = safeElem(source);
      String b = safeElem(target);
      if (!a.isEmpty() && !b.isEmpty()) {
         List<ElementReactionRegistry.ReactionEffect> list = DIRECTIONAL_EFFECTS.get(makeOrderedKey(a, b));
         return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
      } else {
         return Collections.emptyList();
      }
   }

   public record AttributeEffect(String attributeId, String operation, double value, int duration) {
      public AttributeEffect(String attributeId, String operation, double value, int duration) {
         this.attributeId = attributeId == null ? "" : attributeId.trim();
         this.operation = operation == null ? "add" : operation.trim().toLowerCase();
         this.value = value;
         this.duration = Math.max(0, duration);
      }
   }

   public static class ReactionEffect {
      public final String type;
      public final float multiplier;
      public final String formula;
      public final float radius;
      public final String damageType;
      public final boolean damageAttacker;
      public final boolean damageVictim;
      public final String elementId;
      public final int elementAmount;
      public final float elementChance;
      public final int elementDuration;
      public final String elementOp;
      public final String potionId;
      public final int potionDuration;
      public final int potionLevel;
      public final float potionChance;

      public ReactionEffect(String type, float multiplier, String formula) {
         this.type = type;
         this.multiplier = multiplier;
         this.formula = formula;
         this.radius = 0.0F;
         this.damageType = null;
         this.damageAttacker = true;
         this.damageVictim = false;
         this.elementId = null;
         this.elementAmount = 0;
         this.elementChance = 0.0F;
         this.elementDuration = 0;
         this.elementOp = null;
         this.potionId = null;
         this.potionDuration = 0;
         this.potionLevel = 0;
         this.potionChance = 0.0F;
      }

      public ReactionEffect(String type, float multiplier, String formula, float radius, String damageType) {
         this.type = type;
         this.multiplier = multiplier;
         this.formula = formula == null ? "" : formula.trim();
         this.radius = radius;
         this.damageType = damageType == null ? "" : damageType.trim();
         this.damageAttacker = false;
         this.damageVictim = true;
         this.elementId = null;
         this.elementAmount = 0;
         this.elementChance = 0.0F;
         this.elementDuration = 0;
         this.elementOp = null;
         this.potionId = null;
         this.potionDuration = 0;
         this.potionLevel = 0;
         this.potionChance = 0.0F;
      }

      public ReactionEffect(String type, float multiplier, String formula, float radius, String damageType, boolean damageAttacker, boolean damageVictim) {
         this.type = type == null ? "" : type.trim().toLowerCase();
         this.multiplier = multiplier;
         this.formula = formula == null ? "" : formula.trim();
         this.radius = radius;
         this.damageType = damageType == null ? "" : damageType.trim();
         this.damageAttacker = damageAttacker;
         this.damageVictim = damageVictim;
         this.elementId = null;
         this.elementAmount = 0;
         this.elementChance = 0.0F;
         this.elementDuration = 0;
         this.elementOp = null;
         this.potionId = null;
         this.potionDuration = 0;
         this.potionLevel = 0;
         this.potionChance = 0.0F;
      }

      public ReactionEffect(String type, String elementId, int elementAmount, float elementChance, int elementDuration) {
         this.type = type == null ? "" : type.trim().toLowerCase();
         this.multiplier = 0.0F;
         this.formula = "";
         this.radius = 0.0F;
         this.damageType = "";
         this.damageAttacker = false;
         this.damageVictim = true;
         this.elementId = elementId == null ? null : elementId.trim().toLowerCase();
         this.elementAmount = elementAmount;
         this.elementChance = elementChance;
         this.elementDuration = elementDuration;
         this.elementOp = this.type;
         this.potionId = null;
         this.potionDuration = 0;
         this.potionLevel = 0;
         this.potionChance = 0.0F;
      }

      public ReactionEffect(String type, String potionId, int potionDuration, int potionLevel, float potionChance) {
         this.type = type == null ? "" : type.trim().toLowerCase();
         this.multiplier = 0.0F;
         this.formula = "";
         this.radius = 0.0F;
         this.damageType = "";
         this.damageAttacker = false;
         this.damageVictim = true;
         this.elementId = null;
         this.elementAmount = 0;
         this.elementChance = 0.0F;
         this.elementDuration = 0;
         this.elementOp = null;
         this.potionId = potionId == null ? null : potionId.trim();
         this.potionDuration = Math.max(0, potionDuration);
         this.potionLevel = Math.max(0, potionLevel);
         this.potionChance = Math.max(0.0F, Math.min(1.0F, potionChance));
      }
   }

   public record TickRule(Map<String, Integer> requirements, Map<String, Integer> consume, List<ElementReactionRegistry.ReactionEffect> effects, int interval) {
      public TickRule(Map<String, Integer> requirements, Map<String, Integer> consume, List<ElementReactionRegistry.ReactionEffect> effects, int interval) {
         this.requirements = requirements == null ? Collections.emptyMap() : Map.copyOf(requirements);
         this.consume = consume == null ? Collections.emptyMap() : Map.copyOf(consume);
         this.effects = effects == null ? Collections.emptyList() : List.copyOf(effects);
         this.interval = Math.max(1, interval);
      }
   }
}
