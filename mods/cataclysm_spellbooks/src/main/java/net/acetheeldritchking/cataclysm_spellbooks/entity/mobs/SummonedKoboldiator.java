package net.acetheeldritchking.cataclysm_spellbooks.entity.mobs;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Kobolediator_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModTag;
import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SummonedKoboldiator extends Kobolediator_Entity implements IMagicSummon {
   protected LivingEntity cachedSummoner;
   protected UUID summonerUUID;

   public SummonedKoboldiator(EntityType entity, Level world) {
      super(entity, world);
      this.xpReward = 0;
   }

   public SummonedKoboldiator(Level level, LivingEntity owner) {
      this((EntityType)CSEntityRegistry.SUMMONED_KOBOLDIATOR.get(), level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(5, new GenericFollowOwnerGoal(this, this::getSummoner, 1.0, 10.0F, 2.0F, true, 50.0F));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 9.0F));
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
            (ParticleOptions)ModParticle.DESERT_GLYPH.get(),
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
      } else if (entityIn != this.getSummoner() && !this.isAlliedHelper(entityIn)) {
         return this.getSummoner() != null && entityIn.getType().is(ModTag.TEAM_ANCIENT_REMNANT)
            ? false
            : super.isAlliedTo(entityIn) || this.isAlliedHelper(entityIn);
      } else {
         return true;
      }
   }

   public static Builder buildAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.28F)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 120.0)
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.STEP_HEIGHT, 1.25)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      if (pPlayer == this.getSummoner()) {
         pPlayer.startRiding(this);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(pPlayer, pHand);
      }
   }

   protected boolean canRide(Entity p_31508_) {
      return true;
   }

   public boolean isVehicle() {
      return true;
   }

   public boolean canBeControlledByRider() {
      return this.getControllingPassenger() instanceof LivingEntity;
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      return (LivingEntity)this.getFirstPassenger();
   }

   public void travel(Vec3 pTravelVector) {
      if (this.isAlive()) {
         if (this.isVehicle() && this.canBeControlledByRider()) {
            LivingEntity livingentity = this.getControllingPassenger();
            this.setYRot(livingentity.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(livingentity.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;
            float f = livingentity.xxa * 0.5F;
            float f1 = livingentity.zza;
            if (f1 <= 0.0F) {
               f1 *= 0.25F;
            }

            if (this.isControlledByLocalInstance()) {
               this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
               super.travel(new Vec3(f, pTravelVector.y, f1));
            } else if (livingentity instanceof Player) {
               this.setDeltaMovement(Vec3.ZERO);
            }

            this.tryCheckInsideBlocks();
         } else {
            super.travel(pTravelVector);
         }
      }
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity pLivingEntity) {
      Direction direction = this.getMotionDirection();
      if (direction.getAxis() == Axis.Y) {
         return super.getDismountLocationForPassenger(pLivingEntity);
      } else {
         int[][] aint = DismountHelper.offsetsForDirection(direction);
         BlockPos blockpos = this.blockPosition();
         MutableBlockPos blockpos$mutable = new MutableBlockPos();
         UnmodifiableIterator var6 = pLivingEntity.getDismountPoses().iterator();

         while (var6.hasNext()) {
            Pose pose = (Pose)var6.next();
            AABB axisalignedbb = pLivingEntity.getLocalBoundsForPose(pose);

            for (int[] aint1 : aint) {
               blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY(), blockpos.getZ() + aint1[1]);
               double d0 = this.level().getBlockFloorHeight(blockpos$mutable);
               if (DismountHelper.isBlockFloorValid(d0)) {
                  Vec3 vec3 = Vec3.upFromBottomCenterOf(blockpos$mutable, d0);
                  if (DismountHelper.canDismountTo(this.level(), pLivingEntity, axisalignedbb.move(vec3))) {
                     pLivingEntity.setPose(pose);
                     return vec3;
                  }
               }
            }
         }

         return super.getDismountLocationForPassenger(pLivingEntity);
      }
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
