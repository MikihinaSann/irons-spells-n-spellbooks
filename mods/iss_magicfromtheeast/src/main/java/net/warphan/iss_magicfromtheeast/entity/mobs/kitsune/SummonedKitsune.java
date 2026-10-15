package net.warphan.iss_magicfromtheeast.entity.mobs.kitsune;

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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class SummonedKitsune extends Fox implements IMagicSummon {
   public SummonedKitsune(EntityType<? extends Fox> pEntityType, Level plevel) {
      super(pEntityType, plevel);
      this.xpReward = 0;
   }

   public SummonedKitsune(Level level, LivingEntity owner) {
      this((EntityType<? extends Fox>)MFTEEntityRegistries.SUMMONED_KITSUNE.get(), level);
      this.setSummoner(owner);
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity) || this.isAlliedHelper(entity);
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(this.level(), ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), 25, 0.4, 0.8, 0.4, 0.03, false);
         this.setRemoved(RemovalReason.DISCARDED);
      }
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   public void remove(RemovalReason reason) {
      super.remove(reason);
   }

   public boolean doHurtTarget(Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(MFTEEffectRegistries.SOULBURN, 60, 0));
         livingEntity.invulnerableTime = 0;
      }

      return Utils.doMeleeAttack(this, entity, MFTESpellRegistries.KITSUNE_PACK_SPELL.get().getDamageSource(this, this.getSummoner()));
   }

   public boolean hurt(DamageSource source, float amount) {
      return this.shouldIgnoreDamage(source) ? false : super.hurt(source, amount);
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SummonedKitsune.KitsuneMeleeAttack(1.2F, true));
      this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(7, new GenericFollowOwnerGoal(this, this::getSummoner, 0.6F, 15.0F, 5.0F, false, 25.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(5, new GenericProtectOwnerTargetGoal(this, this::getSummoner));
   }

   public static Builder prepareAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.MOVEMENT_SPEED, 0.5);
   }

   public void tick() {
      super.tick();
      if (this.tickCount % 10 == 0) {
         for (int i = 0; i < 2; i++) {
            Vec3 pos = new Vec3(Utils.getRandomScaled(1.0), Utils.getRandomScaled(1.0) + 0.2F, Utils.getRandomScaled(1.0)).add(this.position());
            Vec3 random = new Vec3(Utils.getRandomScaled(0.04F), Utils.getRandomScaled(0.04F), Utils.getRandomScaled(0.04F));
            this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, random.x, random.y, random.z);
         }
      }
   }

   public boolean canHoldItem(ItemStack stack) {
      return false;
   }

   public boolean isFood(ItemStack p_28594_) {
      return false;
   }

   public boolean canBeLeashed() {
      return false;
   }

   class KitsuneMeleeAttack extends MeleeAttackGoal {
      public KitsuneMeleeAttack(double p_28720_, boolean p_28721_) {
         super(SummonedKitsune.this, p_28720_, p_28721_);
      }

      protected void checkAndPerformAttack(LivingEntity p_28724_) {
         if (this.canPerformAttack(p_28724_)) {
            this.resetAttackCooldown();
            this.mob.doHurtTarget(p_28724_);
            SummonedKitsune.this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
         }
      }

      public void start() {
         SummonedKitsune.this.setIsInterested(false);
         super.start();
      }

      public boolean canUse() {
         return !SummonedKitsune.this.isSitting()
            && !SummonedKitsune.this.isSleeping()
            && !SummonedKitsune.this.isCrouching()
            && !SummonedKitsune.this.isFaceplanted()
            && super.canUse();
      }
   }
}
