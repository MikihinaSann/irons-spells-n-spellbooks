package com.snackpirate.aeromancy.spells.summon_breeze;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.snackpirate.aeromancy.spells.AASpells;
import com.snackpirate.aeromancy.spells.wind_charge.MagicWindCharge;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericProtectOwnerTargetGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SummonedBreeze extends Breeze implements IMagicSummon, RangedAttackMob {
   public SummonedBreeze(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
      this.xpReward = 0;
   }

   public SummonedBreeze(Level level, LivingEntity entity) {
      super((EntityType)AASpells.Entities.SUMMONED_BREEZE.get(), level);
      SummonManager.setOwner(this, entity);
   }

   protected Brain<?> makeBrain(Dynamic<?> dynamic) {
      return Brain.provider(ImmutableList.of(), ImmutableList.of()).makeBrain(dynamic);
   }

   public void setTarget(@Nullable LivingEntity target) {
      if (target != null && SummonManager.getOwner(this) != null && !target.is(SummonManager.getOwner(this))) {
         super.setTarget(target);
      }
   }

   public static Builder createAttributes() {
      return Breeze.createLivingAttributes().add(Attributes.STEP_HEIGHT, 1.0).add(Attributes.FOLLOW_RANGE, 32.0);
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.setRemoved(RemovalReason.DISCARDED);
      }
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SummonedBreezeGoals.ShootWindCharge(this, 1.0, 40, 10.0F));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.9F, 15.0F, 5.0F, false, 25.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(5, new GenericProtectOwnerTargetGoal(this, this::getSummoner));
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return this.shouldIgnoreDamage(pSource) ? false : super.hurt(pSource, pAmount);
   }

   public boolean isAlliedTo(Entity pEntity) {
      return super.isAlliedTo(pEntity) || this.isAlliedHelper(pEntity);
   }

   public void die(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.die(pDamageSource);
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.target;
   }

   public void performRangedAttack(LivingEntity target, float v) {
      MagicWindCharge charge = new MagicWindCharge(this.level(), this);
      charge.setPos(this.position().add(0.0, this.getEyeHeight() - charge.getBoundingBox().getYsize() * 0.5, 0.0));
      charge.setDamage(1.0F);
      charge.shoot(target.position().subtract(this.getEyePosition()).normalize().scale(0.5));
      this.level().addFreshEntity(charge);
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
   }
}
