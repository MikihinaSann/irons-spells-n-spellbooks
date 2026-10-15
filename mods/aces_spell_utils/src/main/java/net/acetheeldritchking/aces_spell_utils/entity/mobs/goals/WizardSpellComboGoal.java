package net.acetheeldritchking.aces_spell_utils.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;

public class WizardSpellComboGoal extends Goal {
   protected static final int interval = 5;
   protected final PathfinderMob mob;
   protected final IMagicEntity spellCastingMob;
   protected LivingEntity target;
   protected final int attackIntervalMin;
   protected final int attackIntervalMax;
   protected final float attackRadius;
   protected final float attackRadiusSqr;
   protected final int spellListLength;
   protected final List<AbstractSpell> spells;
   protected int attackTime;
   protected int firstSpell = 0;
   protected final float minSpellQuality;
   protected final float maxSpellQuality;

   public WizardSpellComboGoal(
      IMagicEntity abstractSpellCastingMob, List<AbstractSpell> spells, float minQuality, float maxQuality, int pAttackIntervalMin, int pAttackIntervalMax
   ) {
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
      this.spellCastingMob = abstractSpellCastingMob;
      if (abstractSpellCastingMob instanceof PathfinderMob pathfinderMob) {
         this.mob = pathfinderMob;
         this.attackIntervalMin = pAttackIntervalMin;
         this.attackIntervalMax = pAttackIntervalMax;
         this.attackRadius = 20.0F;
         this.attackRadiusSqr = this.attackRadius * this.attackRadius;
         this.minSpellQuality = minQuality;
         this.maxSpellQuality = maxQuality;
         this.spellListLength = spells.toArray().length;
         this.spells = spells;
         this.resetAttackTimer();
      } else {
         throw new IllegalStateException("Unable to add " + this.getClass().getSimpleName() + "to entity, must extend PathfinderMob.");
      }
   }

   public boolean canUse() {
      this.target = this.mob.getTarget();
      if (this.target != null && !this.spellCastingMob.isCasting()) {
         if (this.attackTime <= -5 * (this.spellListLength - 1)) {
            this.resetAttackTimer();
         }

         this.attackTime--;
         return this.attackTime <= 0 && this.attackTime % 5 == 0;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return false;
   }

   public void stop() {
      this.target = null;
      if (this.attackTime > 0) {
         this.attackTime = -this.spellListLength * 5 - 1;
      }
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      if (this.target != null) {
         double distanceSquared = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
         if (distanceSquared < this.attackRadiusSqr) {
            this.mob.getLookControl().setLookAt(this.target, 45.0F, 45.0F);
            if (this.firstSpell < this.spellListLength) {
               AbstractSpell currentSpell = this.spells.get(this.firstSpell);
               int spellLevel = (int)(currentSpell.getMaxLevel() * Mth.lerp(this.mob.getRandom().nextFloat(), this.minSpellQuality, this.maxSpellQuality));
               spellLevel = Math.max(spellLevel, 1);
               this.spellCastingMob.initiateCastSpell(currentSpell, spellLevel);
               this.firstSpell++;
               this.stop();
            }
         }
      }
   }

   protected void resetAttackTimer() {
      this.attackTime = this.mob.getRandom().nextIntBetweenInclusive(this.attackIntervalMin, this.attackIntervalMax);
      this.firstSpell = 0;
   }
}
