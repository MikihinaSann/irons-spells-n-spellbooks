package com.gametechbc.traveloptics.entity.summons;

import com.gametechbc.traveloptics.api.entity.mobs.MagicCursedSummon;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOSpells;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Draugar.Draugr_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SummonedDraugr extends Draugr_Entity implements MagicCursedSummon {
   protected LivingEntity cachedSummoner;
   protected UUID summonerUUID;

   public SummonedDraugr(EntityType<? extends Draugr_Entity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.xpReward = 0;
   }

   public SummonedDraugr(Level level, LivingEntity owner) {
      this((EntityType<? extends Draugr_Entity>)TOEntities.SUMMONED_DRAUGR.get(), level);
      this.setSummoner(owner);
   }

   public void registerGoals() {
      this.goalSelector
         .getAvailableGoals()
         .removeIf(goal -> goal.getGoal() instanceof HurtByTargetGoal || goal.getGoal() instanceof NearestAttackableTargetGoal);
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.9F, 15.0F, 5.0F, false, 25.0F));
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      super.registerGoals();
   }

   public LivingEntity getSummoner() {
      return OwnerHelper.getAndCacheOwner(this.level(), this.cachedSummoner, this.summonerUUID);
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         this.summonerUUID = owner.getUUID();
         this.cachedSummoner = owner;
      }
   }

   public void remove(RemovalReason pReason) {
      super.remove(pReason);
   }

   public void die(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.die(pDamageSource);
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this, TOEffects.CURSED_REVENANTS_TIMER);
      super.onRemovedFromLevel();
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.summonerUUID = OwnerHelper.deserializeOwner(compoundTag);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      OwnerHelper.serializeOwner(compoundTag, this.summonerUUID);
   }

   public boolean doHurtTarget(Entity pEntity) {
      return Utils.doMeleeAttack(this, pEntity, TOSpells.CURSED_REVENANTS_SPELL.get().getDamageSource(this, this.getSummoner()));
   }

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(
            this.level(), (ParticleOptions)ModParticle.CURSED_FLAME.get(), this.getX(), this.getY() + 0.5, this.getZ(), 20, 0.4, 0.8, 0.4, 0.03, false
         );
         this.discard();
      }
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return this.shouldIgnoreDamage(pSource) ? false : super.hurt(pSource, pAmount);
   }

   protected boolean shouldDespawnInPeaceful() {
      return false;
   }
}
