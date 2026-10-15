package net.warphan.iss_magicfromtheeast.entity.spells.summoned_cloud;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.particle.FogParticleOptions;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import org.joml.Vector3f;

public class SummonCloudEntity extends PathfinderMob implements IMagicSummon {
   public SummonCloudEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public SummonCloudEntity(Level level, LivingEntity caster) {
      this((EntityType<? extends PathfinderMob>)MFTEEntityRegistries.SUMMON_CLOUD_ENTITY.get(), level);
      this.setSummoner(caster);
   }

   public void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
   }

   public static Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 5.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.0)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.5);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      Entity entity = this.getFirstPassenger();
      if (entity instanceof Mob) {
         return (Mob)entity;
      } else {
         entity = this.getFirstPassenger();
         return entity instanceof Player ? (Player)entity : null;
      }
   }

   public void tick() {
      if (!this.level.isClientSide) {
         MagicManager.spawnParticles(
            this.level,
            new FogParticleOptions(new Vector3f(0.7764706F, 1.0F, 0.8862745F), 0.4F),
            this.getX(),
            this.getY() - 0.25,
            this.getZ(),
            1,
            0.4,
            -0.2,
            0.4,
            2.0,
            false
         );
         LivingEntity rider = this.getControllingPassenger();
         if (rider != null && !rider.isDeadOrDying()) {
            rider.addEffect(new MobEffectInstance(MFTEEffectRegistries.CLOUD_BLESS_EFFECT, 100, 0, false, false, true));
         }
      }

      super.tick();
   }

   public void setSummoner(@Nullable LivingEntity owner) {
      if (owner != null) {
         SummonManager.setOwner(this, owner);
      }
   }

   public void onUnSummon() {
      if (!this.level.isClientSide) {
         MagicManager.spawnParticles(this.level, ParticleTypes.SCRAPE, this.getX(), this.getY(), this.getZ(), 25, 0.5, 0.5, 0.5, 0.25, false);
         this.setRemoved(RemovalReason.DISCARDED);
      }
   }

   public void die(DamageSource pDamageSource) {
      this.onDeathHelper();
      super.die(pDamageSource);
   }

   public void onRemovedFromLevel() {
      this.onRemovedHelper(this);
      super.onRemovedFromLevel();
   }

   public boolean isAlliedTo(Entity entity) {
      return super.isAlliedTo(entity) || this.isAlliedHelper(entity);
   }

   public boolean hurt(DamageSource source, float amount) {
      return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) ? false : super.hurt(source, amount);
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean canBeLeashed() {
      return false;
   }

   public boolean shouldRiderSit() {
      return false;
   }

   public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
      if (this.isVehicle()) {
         return super.mobInteract(pPlayer, pHand);
      } else {
         if (pPlayer == this.getSummoner()) {
            this.doPlayerRide(pPlayer);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      }
   }

   protected void doPlayerRide(Player pPlayer) {
      if (!this.level.isClientSide) {
         pPlayer.setYRot(this.getYRot());
         pPlayer.setXRot(this.getXRot());
         pPlayer.startRiding(this);
      }
   }

   protected void tickRidden(Player player, Vec3 p_275242_) {
      super.tickRidden(player, p_275242_);
      this.yRotO = this.getYRot();
      this.setYRot(player.getYRot());
      this.setXRot(player.getXRot());
      this.setRot(this.getYRot(), this.getXRot());
      this.yBodyRot = this.yRotO;
      this.yHeadRot = this.getYRot();
   }

   protected Vec3 getRiddenInput(Player player, Vec3 move) {
      float f = player.xxa * 0.5F;
      float f1 = player.zza;
      double f2 = move.y;
      if (f1 <= 0.0F) {
         f1 *= 0.25F;
      }

      if (f1 > 0.0F) {
         f2 = player.getLookAngle().y;
      }

      return new Vec3(f, f2, f1);
   }

   protected float getRiddenSpeed(Player p_278336_) {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.8F;
   }

   protected void positionRider(Entity passanger, MoveFunction moveFunction) {
      if (this.hasPassenger(passanger)) {
         Vec3 vec3 = this.getPassengerRidingPosition(passanger);
         Vec3 vec31 = passanger.getVehicleAttachmentPoint(this);
         moveFunction.accept(passanger, vec3.x - vec31.x, vec3.y + 0.5 - vec31.y, vec3.z - vec31.z);
      }
   }
}
