package com.gametechbc.traveloptics.api.entity.ai;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CastSpellOnLostSightGoal extends Goal {
   protected LivingEntity target;
   protected final double speedModifier;
   protected final int attackIntervalMin;
   protected final int attackIntervalMax;
   protected int cooldownTime;
   protected float attackRadius;
   protected float attackRadiusSqr;
   protected boolean shortCircuitTemp = false;
   protected boolean hasLineOfSight;
   protected int seeTime = 0;
   protected int strafeTime;
   protected boolean strafingClockwise;
   protected int attackTime = -1;
   protected int projectileCount;
   protected AbstractSpell singleUseSpell = SpellRegistry.none();
   protected int singleUseLevel;
   protected boolean isFlying;
   protected boolean allowFleeing;
   protected int fleeCooldown;
   protected final ArrayList<AbstractSpell> attackSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> defenseSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> movementSpells = new ArrayList<>();
   protected final ArrayList<AbstractSpell> supportSpells = new ArrayList<>();
   protected ArrayList<AbstractSpell> lastSpellCategory = this.attackSpells;
   protected float minSpellQuality = 0.1F;
   protected float maxSpellQuality = 0.4F;
   protected boolean drinksPotions;
   protected final PathfinderMob mob;
   protected final IMagicEntity spellCastingMob;

   public CastSpellOnLostSightGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int pAttackInterval) {
      this(abstractSpellCastingMob, pSpeedModifier, pAttackInterval, pAttackInterval);
   }

   public CastSpellOnLostSightGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax) {
      super();
      this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      this.spellCastingMob = abstractSpellCastingMob;
      if (abstractSpellCastingMob instanceof PathfinderMob m) {
         this.mob = m;
         this.speedModifier = pSpeedModifier;
         this.attackIntervalMin = pAttackIntervalMin;
         this.attackIntervalMax = pAttackIntervalMax;
         this.attackRadius = 20.0F;
         this.attackRadiusSqr = this.attackRadius * this.attackRadius;
         this.allowFleeing = true;
      } else {
         throw new IllegalStateException("Unable to add " + this.getClass().getSimpleName() + " to entity, must extend PathfinderMob.");
      }
   }

   public CastSpellOnLostSightGoal setSpells(
      List<AbstractSpell> attackSpells, List<AbstractSpell> defenseSpells, List<AbstractSpell> movementSpells, List<AbstractSpell> supportSpells
   ) {
      this.attackSpells.clear();
      this.defenseSpells.clear();
      this.movementSpells.clear();
      this.supportSpells.clear();
      this.attackSpells.addAll(attackSpells);
      this.defenseSpells.addAll(defenseSpells);
      this.movementSpells.addAll(movementSpells);
      this.supportSpells.addAll(supportSpells);
      return this;
   }

   public CastSpellOnLostSightGoal setSpellQuality(float minSpellQuality, float maxSpellQuality) {
      this.minSpellQuality = minSpellQuality;
      this.maxSpellQuality = maxSpellQuality;
      return this;
   }

   public CastSpellOnLostSightGoal setSingleUseSpell(AbstractSpell abstractSpell, int minDelay, int maxDelay, int minLevel, int maxLevel) {
      this.singleUseSpell = abstractSpell;
      this.singleUseLevel = Utils.random.nextIntBetweenInclusive(minLevel, maxLevel);
      return this;
   }

   public CastSpellOnLostSightGoal setIsFlying() {
      this.isFlying = true;
      return this;
   }

   public CastSpellOnLostSightGoal setDrinksPotions() {
      this.drinksPotions = true;
      return this;
   }

   public CastSpellOnLostSightGoal setAllowFleeing(boolean allowFleeing) {
      this.allowFleeing = allowFleeing;
      return this;
   }

   public boolean canUse() {
      LivingEntity livingentity = this.mob.getTarget();
      if (livingentity != null && livingentity.isAlive()) {
         this.target = livingentity;
         this.hasLineOfSight = this.mob.getSensing().hasLineOfSight(this.target);
         return !this.hasLineOfSight;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.canUse() || this.target != null && this.target.isAlive() && !this.mob.getNavigation().isDone();
   }

   public void stop() {
      this.target = null;
      this.seeTime = 0;
      this.attackTime = -1;
      this.mob.setAggressive(false);
      this.mob.getMoveControl().strafe(0.0F, 0.0F);
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      if (this.target != null) {
         boolean currentlyHasLineOfSight = this.mob.getSensing().hasLineOfSight(this.target);
         if (!currentlyHasLineOfSight) {
            this.seeTime++;
            if (this.seeTime >= 60 && this.cooldownTime <= 0) {
               this.castSpellWhenOutOfSight();
               this.seeTime = 0;
            }
         } else {
            this.seeTime = 0;
         }

         this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
         this.cooldownTime--;
      }
   }

   protected void castSpellWhenOutOfSight() {
      if (!this.spellCastingMob.isCasting() && !this.spellCastingMob.isDrinkingPotion()) {
         AbstractSpell spell = this.getNextSpellType();
         int spellLevel = (int)(spell.getMaxLevel() * Mth.lerp(this.mob.getRandom().nextFloat(), this.minSpellQuality, this.maxSpellQuality));
         spellLevel = Math.max(spellLevel, 1);
         if (!spell.shouldAIStopCasting(spellLevel, this.mob, this.target)) {
            this.spellCastingMob.initiateCastSpell(spell, spellLevel);
            this.cooldownTime = Mth.floor(Mth.lerp(this.mob.getRandom().nextDouble(), this.attackIntervalMin, this.attackIntervalMax));
         }
      }
   }

   protected void handleAttackLogic(double distanceSquared) {
      if (this.seeTime >= -50) {
         if (--this.attackTime == 0) {
            this.resetAttackTimer(distanceSquared);
            if (!this.spellCastingMob.isCasting() && !this.spellCastingMob.isDrinkingPotion()) {
               this.doSpellAction();
            }
         } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(distanceSquared) / this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
         }

         if (this.spellCastingMob.isCasting()) {
            SpellData spellData = MagicData.getPlayerMagicData(this.mob).getCastingSpell();
            if (this.target.isDeadOrDying() || spellData.getSpell().shouldAIStopCasting(spellData.getLevel(), this.mob, this.target)) {
               this.spellCastingMob.cancelCast();
            }
         }
      }
   }

   protected void resetAttackTimer(double distanceSquared) {
      float f = (float)Math.sqrt(distanceSquared) / this.attackRadius;
      this.attackTime = Mth.floor(f * (this.attackIntervalMax - this.attackIntervalMin) + this.attackIntervalMin);
   }

   protected void doMovement(double distanceSquared) {
      double speed = (this.spellCastingMob.isCasting() ? 0.75F : 1.0F) * this.movementSpeed();
      this.mob.lookAt(this.target, 30.0F, 30.0F);
      float fleeDist = 0.275F;
      if (this.allowFleeing
         && !this.spellCastingMob.isCasting()
         && this.attackTime > 10
         && --this.fleeCooldown <= 0
         && distanceSquared < this.attackRadiusSqr * fleeDist * fleeDist) {
         Vec3 flee = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.target.position());
         if (flee != null) {
            this.mob.getNavigation().moveTo(flee.x, flee.y, flee.z, speed * 1.5);
         } else {
            this.mob.getMoveControl().strafe(-((float)speed), (float)speed);
         }
      } else if (distanceSquared < this.attackRadiusSqr && this.seeTime >= 5) {
         this.mob.getNavigation().stop();
         if (++this.strafeTime > 25 && this.mob.getRandom().nextDouble() < 0.1) {
            this.strafingClockwise = !this.strafingClockwise;
            this.strafeTime = 0;
         }

         float strafeForward = (distanceSquared * 6.0 < this.attackRadiusSqr ? -1.0F : 0.5F) * 0.2F * (float)this.speedModifier;
         int strafeDir = this.strafingClockwise ? 1 : -1;
         this.mob.getMoveControl().strafe(strafeForward, (float)speed * strafeDir);
         if (this.mob.horizontalCollision && this.mob.getRandom().nextFloat() < 0.1F) {
            this.tryJump();
         }
      } else if (this.mob.tickCount % 5 == 0) {
         if (this.isFlying) {
            this.mob.getMoveControl().setWantedPosition(this.target.getX(), this.target.getY() + 2.0, this.target.getZ(), this.speedModifier);
         } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
         }
      }
   }

   protected double movementSpeed() {
      return this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * 2.0;
   }

   protected void tryJump() {
      Vec3 nextBlock = new Vec3(this.mob.xxa, 0.0, this.mob.zza).normalize();
      BlockPos blockpos = BlockPos.containing(this.mob.position().add(nextBlock));
      BlockState blockstate = this.mob.level().getBlockState(blockpos);
      VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.level(), blockpos);
      if (!voxelshape.isEmpty() && !blockstate.is(BlockTags.DOORS) && !blockstate.is(BlockTags.FENCES)) {
         BlockPos blockposAbove = blockpos.above();
         BlockState blockstateAbove = this.mob.level().getBlockState(blockposAbove);
         VoxelShape voxelshapeAbove = blockstateAbove.getCollisionShape(this.mob.level(), blockposAbove);
         if (voxelshapeAbove.isEmpty()) {
            this.mob.getJumpControl().jump();
            this.mob.setXxa(this.mob.xxa * 5.0F);
            this.mob.setZza(this.mob.zza * 5.0F);
         }
      }
   }

   protected void doSpellAction() {
      if (!this.spellCastingMob.getHasUsedSingleAttack() && this.singleUseSpell != SpellRegistry.none() && this.singleUseLevel > 0) {
         this.spellCastingMob.setHasUsedSingleAttack(true);
         this.spellCastingMob.initiateCastSpell(this.singleUseSpell, this.singleUseLevel);
         this.fleeCooldown = 7 + this.singleUseSpell.getCastTime(this.singleUseLevel);
      } else {
         AbstractSpell spell = this.getNextSpellType();
         int spellLevel = (int)(spell.getMaxLevel() * Mth.lerp(this.mob.getRandom().nextFloat(), this.minSpellQuality, this.maxSpellQuality));
         spellLevel = Math.max(spellLevel, 1);
         if (!spell.shouldAIStopCasting(spellLevel, this.mob, this.target)) {
            this.spellCastingMob.initiateCastSpell(spell, spellLevel);
            this.fleeCooldown = 7 + spell.getCastTime(spellLevel);
         } else {
            this.attackTime = 5;
         }
      }
   }

   protected AbstractSpell getNextSpellType() {
      NavigableMap<Integer, ArrayList<AbstractSpell>> weightedSpells = new TreeMap<>();
      int attackWeight = this.getAttackWeight();
      int defenseWeight = this.getDefenseWeight() - (this.lastSpellCategory == this.defenseSpells ? 100 : 0);
      int movementWeight = this.getMovementWeight() - (this.lastSpellCategory == this.movementSpells ? 50 : 0);
      int supportWeight = this.getSupportWeight() - (this.lastSpellCategory == this.supportSpells ? 100 : 0);
      int total = 0;
      if (!this.attackSpells.isEmpty() && attackWeight > 0) {
         total += attackWeight;
         weightedSpells.put(total, this.attackSpells);
      }

      if (!this.defenseSpells.isEmpty() && defenseWeight > 0) {
         total += defenseWeight;
         weightedSpells.put(total, this.defenseSpells);
      }

      if (!this.movementSpells.isEmpty() && movementWeight > 0) {
         total += movementWeight;
         weightedSpells.put(total, this.movementSpells);
      }

      if ((!this.supportSpells.isEmpty() || this.drinksPotions) && supportWeight > 0) {
         total += supportWeight;
         weightedSpells.put(total, this.supportSpells);
      }

      if (total <= 0) {
         return SpellRegistry.none();
      } else {
         int seed = this.mob.getRandom().nextInt(total);
         ArrayList<AbstractSpell> spellList = weightedSpells.higherEntry(seed).getValue();
         this.lastSpellCategory = spellList;
         if (this.drinksPotions && spellList == this.supportSpells && (this.supportSpells.isEmpty() || this.mob.getRandom().nextFloat() < 0.5F)) {
            this.spellCastingMob.startDrinkingPotion();
            return SpellRegistry.none();
         } else {
            return spellList.get(this.mob.getRandom().nextInt(spellList.size()));
         }
      }
   }

   public void start() {
      super.start();
      this.mob.setAggressive(true);
   }

   protected int getAttackWeight() {
      int baseWeight = 80;
      if (this.hasLineOfSight && this.target != null) {
         float targetHealth = this.target.getHealth() / this.target.getMaxHealth();
         int targetHealthWeight = (int)((1.0F - targetHealth) * baseWeight * 0.75F);
         double distanceSquared = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
         int distanceWeight = (int)(1.0 - distanceSquared / this.attackRadiusSqr * -60.0);
         return baseWeight + targetHealthWeight + distanceWeight;
      } else {
         return 0;
      }
   }

   protected int getDefenseWeight() {
      int baseWeight = -20;
      if (this.target == null) {
         return baseWeight;
      } else {
         float x = this.mob.getHealth();
         float m = this.mob.getMaxHealth();
         int healthWeight = (int)(50.0F * (-(x * x * x) / (m * m * m) + 1.0F));
         float targetHealth = this.target.getHealth() / this.target.getMaxHealth();
         int targetHealthWeight = (int)(1.0F - targetHealth) * -35;
         int threatWeight = this.projectileCount * 95;
         return baseWeight + healthWeight + targetHealthWeight + threatWeight;
      }
   }

   protected int getMovementWeight() {
      if (this.target == null) {
         return 0;
      } else {
         double distanceSquared = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
         double distancePercent = Mth.clamp(distanceSquared / this.attackRadiusSqr, 0.0, 1.0);
         int distanceWeight = (int)(distancePercent * 50.0);
         int losWeight = this.hasLineOfSight ? 0 : 80;
         float healthInverted = 1.0F - this.mob.getHealth() / this.mob.getMaxHealth();
         float distanceInverted = (float)(1.0 - distancePercent);
         int runWeight = (int)(400.0F * healthInverted * healthInverted * distanceInverted * distanceInverted);
         return distanceWeight + losWeight + runWeight;
      }
   }

   protected int getSupportWeight() {
      int baseWeight = -15;
      if (this.target == null) {
         return baseWeight;
      } else {
         float health = 1.0F - this.mob.getHealth() / this.mob.getMaxHealth();
         int healthWeight = (int)(200.0F * health);
         double distanceSquared = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
         double distancePercent = Mth.clamp(distanceSquared / this.attackRadiusSqr, 0.0, 1.0);
         int distanceWeight = (int)((1.0 - distancePercent) * -75.0);
         return baseWeight + healthWeight + distanceWeight;
      }
   }
}
