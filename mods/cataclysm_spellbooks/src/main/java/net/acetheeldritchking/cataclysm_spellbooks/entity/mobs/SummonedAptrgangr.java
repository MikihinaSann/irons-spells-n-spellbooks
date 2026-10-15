package net.acetheeldritchking.cataclysm_spellbooks.entity.mobs;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Draugar.Aptrgangr_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericCopyOwnerTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import java.util.UUID;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SummonedAptrgangr extends Aptrgangr_Entity implements IMagicSummon {
   protected LivingEntity cachedSummoner;
   protected UUID summonerUUID;

   public SummonedAptrgangr(EntityType entity, Level world) {
      super(entity, world);
   }

   public SummonedAptrgangr(Level level, LivingEntity owner) {
      this((EntityType)CSEntityRegistry.SUMMONED_APTRGANGR.get(), level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(3, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0, 10.0F, 2.0F, false, 50.0F));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Mob.class, 9.0F));
      this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0, 80));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(3, new GenericCopyOwnerTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(4, new GenericHurtByTargetGoal(this, entity -> entity == this.getSummoner()).setAlertOthers(new Class[0]));
      super.registerGoals();
      this.goalSelector
         .getAvailableGoals()
         .removeIf(goal -> goal.getGoal() instanceof HurtByTargetGoal || goal.getGoal() instanceof NearestAttackableTargetGoal);
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      return this.shouldIgnoreDamage(pSource) ? false : super.hurt(pSource, pAmount);
   }

   public void die(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.die(pDamageSource);
   }

   protected boolean shouldDropLoot() {
      return false;
   }

   public void onUnSummon() {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(
            this.level(),
            (ParticleOptions)ModParticle.CURSED_FLAME.get(),
            this.getX(),
            this.getY(),
            this.getZ(),
            25,
            this.level().random.nextGaussian() * 0.007,
            this.level().random.nextGaussian() * 0.009,
            this.level().random.nextGaussian() * 0.007,
            0.1,
            false
         );
         this.discard();
      }
   }

   public boolean isAlliedTo(Entity entityIn) {
      if (entityIn == this) {
         return true;
      } else if (entityIn == this.getSummoner() || this.isAlliedHelper(entityIn)) {
         return true;
      } else {
         return this.getSummoner() != null && !entityIn.isAlliedTo(this.getSummoner()) ? false : this.getTeam() == null && entityIn.getTeam() == null;
      }
   }

   public static Builder buildAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.28F)
         .add(Attributes.ATTACK_DAMAGE, 12.0)
         .add(Attributes.MAX_HEALTH, 110.0)
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.STEP_HEIGHT, 1.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.summonerUUID = OwnerHelper.deserializeOwner(pCompound);
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      OwnerHelper.serializeOwner(pCompound, this.summonerUUID);
   }
}
