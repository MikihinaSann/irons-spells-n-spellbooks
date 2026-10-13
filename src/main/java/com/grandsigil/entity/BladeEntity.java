package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.ArsenalParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.fx.ArsenalFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BladeEntity extends Entity {
   public static final int STATE_EMERGE = 0;
   public static final int STATE_FLY = 1;
   public static final int STATE_STUCK = 2;
   public static final int STATE_FADE = 3;
   public static final int EMERGE_TICKS = 9;
   public static final int COLOSSUS_EMERGE = 26;
   public static final int FADE_TICKS = 14;
   private static final double START_SPEED = 0.6;
   private static final double MAX_SPEED = 3.4;
   private static final double ACCEL = 0.28;
   public static final float SWORD = 2.2F;
   public static final float COLOSSUS = 4.5F;
   public static final float COLOSSUS_BLAST = 11.0F;
   public static final float COLOSSUS_SHARE = 1.6F;
   private static final double TURN = 0.16;
   private static final double TURN_MAX = 0.55;
   private static final int FLY_LIFE = 80;
   private static final int STUCK_BODY = 12;
   private static final int STUCK_GROUND = 70;
   private static final int STUCK_COLOSSUS = 110;
   public static final float BLADE = 0.075F;
   private static final EntityDataAccessor<Vector3f> DATA_DIR = SynchedEntityData.defineId(BladeEntity.class, EntityDataSerializers.VECTOR3);
   private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(BladeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STATE_AGE = SynchedEntityData.defineId(BladeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_INDEX = SynchedEntityData.defineId(BladeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(BladeEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   @Nullable
   private ArsenalEntity armoury;
   private int targetId = -1;
   private Vec3 aim = Vec3.ZERO;
   private float power = 22.0F;
   private double speed;
   private int stateAge;
   private boolean counted;
   @Nullable
   private Entity stuckTo;
   private Vec3 stuckOffset = Vec3.ZERO;

   public BladeEntity(EntityType<? extends BladeEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public BladeEntity(Level level, ArsenalEntity armoury, Vec3 from, Vec3 out, @Nullable LivingEntity target, Vec3 aim, float power, int index) {
      this(GsEntities.BLADE.get(), level);
      this.setPos(from.x, from.y, from.z);
      this.armoury = armoury;
      this.casterId = armoury.casterId();
      this.targetId = target == null ? -1 : target.getId();
      this.aim = aim;
      this.power = power;
      this.entityData.set(DATA_DIR, new Vector3f((float)out.x, (float)out.y, (float)out.z));
      this.entityData.set(DATA_INDEX, index);
   }

   public static BladeEntity colossus(Level level, ArsenalEntity armoury, Vec3 from, Vec3 aim, float power) {
      Vec3 out = aim.subtract(from);
      out = out.lengthSqr() < 1.0E-4 ? new Vec3(0.0, -1.0, 0.0) : out.normalize();
      BladeEntity blade = new BladeEntity(level, armoury, from, out, null, aim, power, 1);
      blade.entityData.set(DATA_SIZE, 4.5F);
      return blade;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_DIR, new Vector3f(0.0F, 0.0F, 1.0F));
      builder.define(DATA_STATE, 0);
      builder.define(DATA_STATE_AGE, 0);
      builder.define(DATA_INDEX, 0);
      builder.define(DATA_SIZE, 1.0F);
   }

   public float size() {
      return this.entityData.get(DATA_SIZE);
   }

   public boolean colossal() {
      return this.size() > 1.5F;
   }

   public static float length(int index) {
      return switch (Math.floorMod(index, 3)) {
         case 0 -> 2.5F;
         case 1 -> 3.1F;
         default -> 3.6F;
      };
   }

   public float reach() {
      return length(this.index()) * 2.2F * this.size();
   }

   private int emergeTicks() {
      return this.colossal() ? 26 : 9;
   }

   public Vector3f dir() {
      return this.entityData.get(DATA_DIR);
   }

   public int state() {
      return this.entityData.get(DATA_STATE);
   }

   public int stateAge() {
      return this.stateAge;
   }

   public int index() {
      return this.entityData.get(DATA_INDEX);
   }

   public float solid(float partial) {
      return this.state() != 3 ? 1.0F : 1.0F - Mth.clamp(((float)this.stateAge() + partial) / 14.0F, 0.0F, 1.0F);
   }

   public float drawn(float partial) {
      return this.state() != 0 ? 1.0F : Mth.clamp(((float)this.stateAge() + partial) / (float)this.emergeTicks(), 0.0F, 1.0F);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("BladeEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(13);
      }

      if (this.level().isClientSide) {
         this.stateAge++;
         int served = this.entityData.get(DATA_STATE_AGE);
         if (Math.abs(served - this.stateAge) > 2) {
            this.stateAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            ArsenalParticles.blade(this);
         }
      } else {
         this.stateAge++;
         this.entityData.set(DATA_STATE_AGE, this.stateAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.state()) {
            case 0:
               Vector3f d = this.dir();
               double step = (double)(this.reach() / (float)this.emergeTicks());
               this.setPos(this.getX() + (double)d.x * step, this.getY() + (double)d.y * step, this.getZ() + (double)d.z * step);
               if (this.stateAge >= this.emergeTicks()) {
                  this.speed = this.colossal() ? 0.2 : 0.6;
                  this.to(1);
               }
               break;
            case 1:
               this.fly(level);
               if (this.state() == 1 && this.stateAge >= 80) {
                  this.to(3);
               }
               break;
            case 2:
               if (this.stuckTo != null) {
                  if (!this.stuckTo.isAlive() || this.stuckTo.isRemoved()) {
                     this.to(3);
                     return;
                  }

                  Vec3 at = this.stuckTo.position().add(this.stuckOffset);
                  this.setPos(at.x, at.y, at.z);
               }

               int hold = this.stuckTo != null ? 12 : (this.colossal() ? 110 : 70);
               if (this.stateAge >= hold) {
                  this.to(3);
               }
               break;
            default:
               if (this.stateAge >= 14) {
                  this.discard();
               }
         }
      }
   }

   private void fly(ServerLevel level) {
      if ((this.targetId < 0 ? null : level.getEntity(this.targetId)) instanceof LivingEntity living && living.isAlive()) {
         this.aim = living.getBoundingBox().getCenter();
      }

      Vector3f d = this.dir();
      Vec3 heading = new Vec3((double)d.x, (double)d.y, (double)d.z);
      Vec3 want = this.aim.subtract(this.position());
      if (want.lengthSqr() > 1.0E-4) {
         want = want.normalize();
         double turn = Math.min(0.55, 0.16 + 0.03 * (double)this.stateAge);
         double angle = Math.acos(Mth.clamp(heading.dot(want), -1.0, 1.0));
         heading = angle <= turn ? want : heading.scale(1.0 - turn / angle).add(want.scale(turn / angle)).normalize();
      }

      this.speed = Math.min(3.4, this.speed + 0.28);
      this.entityData.set(DATA_DIR, new Vector3f((float)heading.x, (float)heading.y, (float)heading.z));
      Vec3 from = this.position();
      Vec3 to = from.add(heading.scale(this.speed));
      BlockHitResult block = level.clip(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, this));
      double nearest = block.getType() == Type.MISS ? Double.MAX_VALUE : from.distanceToSqr(block.getLocation());
      Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
      if (this.colossal()) {
         if (block.getType() != Type.MISS) {
            Vec3 at = block.getLocation().add(heading.scale(2.6));
            this.setPos(at.x, at.y, at.z);
            this.stuckTo = null;
            this.impact(level, block.getLocation(), caster);
            this.to(2);
         } else {
            this.setPos(to.x, to.y, to.z);
         }
      } else {
         LivingEntity struck = null;
         Vec3 struckAt = null;

         for (LivingEntity body : level.getEntitiesOfClass(
            LivingEntity.class, new AABB(from, to).inflate(1.8), e -> ArsenalEntity.hostile(e, caster, this.casterId)
         )) {
            Optional<Vec3> crossed = body.getBoundingBox().inflate(0.7).clip(from, to);
            if (crossed.isPresent()) {
               double dist = from.distanceToSqr(crossed.get());
               if (dist < nearest) {
                  nearest = dist;
                  struck = body;
                  struckAt = crossed.get();
               }
            }
         }

         if (struck != null) {
            this.strike(level, struck, struckAt, heading, caster);
         } else if (block.getType() != Type.MISS) {
            Vec3 at = block.getLocation().add(heading.scale(1.6));
            this.setPos(at.x, at.y, at.z);
            this.stuckTo = null;
            ArsenalFx.ground(level, block.getLocation(), this.index());
            this.to(2);
         } else {
            this.setPos(to.x, to.y, to.z);
         }
      }
   }

   private void strike(ServerLevel level, LivingEntity body, Vec3 at, Vec3 heading, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      body.invulnerableTime = 0;
      body.hurt(GsDamage.thousandBlades(level, caster), this.power * 0.075F * scale);
      body.push(heading.x * 0.28, 0.06, heading.z * 0.28);
      body.hurtMarked = true;
      Vec3 tip = at.add(heading.scale(1.0));
      this.stuckTo = body;
      this.stuckOffset = tip.subtract(body.position());
      this.setPos(tip.x, tip.y, tip.z);
      ArsenalFx.hit(level, at, this.index());
      if (this.armoury != null && !this.armoury.isRemoved()) {
         this.armoury.landed();
      }

      this.to(2);
   }

   private void impact(ServerLevel level, Vec3 at, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float reach = 16.5F;
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;
      ArsenalFx.colossus(level, at);

      for (LivingEntity body : level.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate((double)reach), e -> ArsenalEntity.hostile(e, caster, this.casterId)
      )) {
         double d = body.position().distanceTo(at);
         if (!(d > (double)reach) && caught < cap) {
            caught++;
            float falloff = d <= 11.0 ? 1.0F : (float)(1.0 - (d - 11.0) / (double)(reach - 11.0F));
            body.invulnerableTime = 0;
            body.hurt(GsDamage.thousandBlades(level, caster), this.power * 1.6F * scale * falloff);
            Vec3 push = body.position().subtract(at);
            Vec3 flat = new Vec3(push.x, 0.0, push.z);
            flat = flat.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : flat.normalize();
            body.push(flat.x * 1.8 * (double)falloff, 0.8 * (double)falloff, flat.z * 1.8 * (double)falloff);
            body.hurtMarked = true;
         }
      }
   }

   private void to(int state) {
      this.entityData.set(DATA_STATE, state);
      this.stateAge = 0;
      this.entityData.set(DATA_STATE_AGE, 0);
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(13);
      }

      super.remove(reason);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (this.level().isClientSide && DATA_STATE.equals(key)) {
         this.stateAge = this.entityData.get(DATA_STATE_AGE);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.discard();
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity tracker) {
      return super.getAddEntityPacket(tracker);
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 36864.0;
   }

   @Override
   public boolean isAttackable() {
      return false;
   }

   @Override
   protected boolean canRide(Entity vehicle) {
      return false;
   }
}
