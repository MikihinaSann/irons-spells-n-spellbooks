package net.warphan.iss_magicfromtheeast.entity.mobs.jiangshi;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericProtectOwnerTargetGoal;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class SummonedJiangshiEntity extends JiangshiEntity implements IMagicSummon {
   public SummonedJiangshiEntity(EntityType<? extends SummonedJiangshiEntity> pEntityType, Level plevel) {
      super(pEntityType, plevel);
      this.xpReward = 0;
   }

   public SummonedJiangshiEntity(Level level, LivingEntity owner, boolean playRiseAnimation) {
      this((EntityType<? extends SummonedJiangshiEntity>)MFTEEntityRegistries.SUMMONED_JIANGSHI.get(), level);
      this.setSummoner(owner);
      if (playRiseAnimation) {
         this.triggerRiseAnimation();
      }
   }

   @Override
   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new JiangshiEntity.JiangshiAttackGoal(this, 1.0, 5, 10));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.6F, 15.0F, 5.0F, false, 30.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(5, new GenericProtectOwnerTargetGoal(this, this::getSummoner));
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.ARMOR, 4.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.STEP_HEIGHT, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
         .add(Attributes.SAFE_FALL_DISTANCE, 7.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 1.8F);
   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity) || this.isAlliedHelper(entity);
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   public void remove(RemovalReason reason) {
      super.remove(reason);
   }

   @Override
   public boolean doHurtTarget(Entity entity) {
      return Utils.doMeleeAttack(this, entity, MFTESpellRegistries.JIANGSHI_INVOKE_SPELL.get().getDamageSource(this, this.getSummoner()));
   }

   @Override
   public boolean hurt(DamageSource source, float amount) {
      return (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !this.isAnimatingRise())
            && !this.shouldIgnoreDamage(source)
            && !source.is(DamageTypes.DROWN)
         ? super.hurt(source, amount)
         : false;
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.setRemoved(RemovalReason.DISCARDED);
      }
   }

   public boolean isPushable() {
      return super.isPushable() && this.isAnimatingRise();
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isAnimatingRise();
   }
}
