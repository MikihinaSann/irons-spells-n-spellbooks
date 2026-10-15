package net.alshanex.familiarslib.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.alshanex.familiarslib.registry.CriteriaTriggersRegistry;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance;
import net.minecraft.server.level.ServerPlayer;

public class SimpleAdvancementTrigger extends SimpleCriterionTrigger<SimpleAdvancementTrigger.TriggerInstance> {
   public Codec<SimpleAdvancementTrigger.TriggerInstance> codec() {
      return SimpleAdvancementTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer player) {
      this.trigger(player, instance -> true);
   }

   public record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {
      public static final Codec<SimpleAdvancementTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         instance -> instance.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SimpleAdvancementTrigger.TriggerInstance::player))
            .apply(instance, SimpleAdvancementTrigger.TriggerInstance::new)
      );

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameArchmage() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_ARCHMAGE_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameCleric() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_CLERIC_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameDruid() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_DRUID_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameHunter() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_HUNTER_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameIllusionist() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_ILLUSIONIST_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameMage() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_MAGE_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameNecromancer() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_NECROMANCER_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tamePlague() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_PLAGUE_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameScorcher() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_SCORCHER_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameSummoner() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_SUMMONER_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameBard() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_BARD_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameFrostling() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_FROSTLING_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> tameDragoon() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.TAMING_DRAGOON_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> consumableUse() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.CONSUMABLE_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> shardUse() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.SHARD_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }

      public static Criterion<SimpleAdvancementTrigger.TriggerInstance> illusionistReveal() {
         return ((SimpleAdvancementTrigger)CriteriaTriggersRegistry.ILLUSIONIST_REVEAL_TRIGGER.get())
            .createCriterion(new SimpleAdvancementTrigger.TriggerInstance(Optional.empty()));
      }
   }
}
