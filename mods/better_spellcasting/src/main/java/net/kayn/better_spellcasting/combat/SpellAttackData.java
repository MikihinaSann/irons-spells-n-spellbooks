package net.kayn.better_spellcasting.combat;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import org.jetbrains.annotations.NotNull;

public class SpellAttackData {
   private final AbstractSpell spell;
   private final int level;
   private final String targetType;
   private final SpellAttackData.Trigger trigger;

   public SpellAttackData(AbstractSpell spell, int level, String targetType, SpellAttackData.Trigger trigger) {
      this.spell = spell;
      this.level = level;
      this.targetType = targetType;
      this.trigger = trigger == null ? SpellAttackData.Trigger.ON_HIT : trigger;
   }

   public AbstractSpell getSpell() {
      return this.spell;
   }

   public int getLevel() {
      return this.level;
   }

   public String getTargetType() {
      return this.targetType;
   }

   public SpellAttackData.Trigger getTrigger() {
      return this.trigger;
   }

   public boolean isSelfCast() {
      return "SELF".equalsIgnoreCase(this.targetType);
   }

   public boolean isOnAttack() {
      return this.trigger == SpellAttackData.Trigger.ON_ATTACK;
   }

   public boolean isOnHit() {
      return this.trigger == SpellAttackData.Trigger.ON_HIT;
   }

   @Override
   public String toString() {
      return "SpellAttackData{spell="
         + (this.spell != null ? this.spell.getSpellId() : "null")
         + ", level="
         + this.level
         + ", targetType='"
         + this.targetType
         + "', trigger="
         + this.trigger
         + "}";
   }

   public static enum Trigger {
      ON_ATTACK,
      ON_HIT;

      @NotNull
      public static SpellAttackData.Trigger fromString(String s) {
         if (s == null) {
            return ON_HIT;
         } else {
            String n = s.trim().toLowerCase();

            return switch (n) {
               case "on_attack", "onattack", "attack", "on-attack" -> ON_ATTACK;
               case "on_hit", "onhit", "hit", "on-hit" -> ON_HIT;
               default -> ON_HIT;
            };
         }
      }
   }
}
