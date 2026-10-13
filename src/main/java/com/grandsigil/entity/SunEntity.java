package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.SunParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.SunFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.List;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class SunEntity extends Entity {
   public static final float SKY_DISTANCE = 100.0F;
   public static final float APPARENT = 0.3F;
   public static final int STAGE_SHIFT = 0;
   public static final int STAGE_CHARGE = 1;
   public static final int STAGE_FIRE = 2;
   public static final int STAGE_AFTER = 3;
   public static final int SHIFT_TICKS = 80;
   public static final int CHARGE_TICKS = 60;
   public static final int FIRE_TICKS = 16;
   public static final int AFTER_TICKS = 70;
   private static final int SCORCH_INTERVAL = 10;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(SunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(SunEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SunEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_ANGLE = SynchedEntityData.defineId(SunEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private boolean counted;
   private int stageAge;

   public SunEntity(EntityType<? extends SunEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public SunEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius, float sunAngle) {
      this(GsEntities.SUN.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
      this.entityData.set(DATA_ANGLE, sunAngle);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 30.0F);
      builder.define(DATA_ANGLE, 0.0F);
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   public float radius() {
      return this.entityData.get(DATA_RADIUS);
   }

   public float startAngle() {
      return this.entityData.get(DATA_ANGLE);
   }

   public static Vector3f sunDirection(float sunAngle) {
      return turned(0.0F, 1.0F, 0.0F, sunAngle);
   }

   public static Vector3f sunRight(float sunAngle) {
      return turned(1.0F, 0.0F, 0.0F, sunAngle);
   }

   public static Vector3f sunUp(float sunAngle) {
      return turned(0.0F, 0.0F, 1.0F, sunAngle);
   }

   private static Vector3f turned(float x, float y, float z, float sunAngle) {
      Vector3f out = new Vector3f(x, y, z);
      out.rotateX(sunAngle);
      out.rotateY((float) (-Math.PI / 2));
      return out;
   }

   public float angle(float partial) {
      float start = this.startAngle();
      float noon = start > (float) Math.PI ? (float) (Math.PI * 2) : 0.0F;
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> Mth.lerp(ease(Mth.clamp(age / 80.0F, 0.0F, 1.0F)), start, noon);
         case 3 -> {
            float t = Mth.clamp((age - 20.0F) / 50.0F, 0.0F, 1.0F);
            yield Mth.lerp(ease(t), noon, start);
         }
         default -> noon;
      };
   }

   private static float ease(float t) {
      return t * t * (3.0F - 2.0F * t);
   }

   public float swell(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> Mth.lerp(ease(Mth.clamp(age / 80.0F, 0.0F, 1.0F)), 1.0F, 1.5F);
         case 1 -> Mth.lerp(ease(Mth.clamp(age / 60.0F, 0.0F, 1.0F)), 1.5F, 3.2F);
         case 2 -> 3.2F;
         default -> Mth.lerp(Mth.clamp(age / 42.0F, 0.0F, 1.0F), 3.2F, 1.0F);
      };
   }

   public Vec3 sunCentre(float partial) {
      Vector3f direction = sunDirection(this.angle(partial));
      return this.position().add((double)(direction.x * 100.0F), (double)(direction.y * 100.0F), (double)(direction.z * 100.0F));
   }

   public float sunRadius(float partial) {
      return 30.000002F * this.swell(partial);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("SunEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(1);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            SunParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickShift(level);
               break;
            case 1:
               this.tickCharge(level);
               break;
            case 2:
               this.tickFire(level);
               break;
            default:
               this.tickAfter(level);
         }
      }
   }

   private void tickShift(ServerLevel level) {
      if (this.stageAge == 1) {
         SunFx.taken(level, this.position());
      }

      if (this.stageAge % 10 == 0) {
         SunFx.climbing(level, this.position(), (float)this.stageAge / 80.0F);
      }

      if (this.stageAge >= 80) {
         this.advance(1);
      }
   }

   private void tickCharge(ServerLevel level) {
      if (this.stageAge == 1) {
         SunFx.noon(level, this.position(), (double)this.radius());
      }

      if (this.stageAge % 10 == 0) {
         this.scorch(level);
      }

      if (this.stageAge % 15 == 0) {
         SunFx.charging(level, this.position(), (double)this.radius(), (float)this.stageAge / 60.0F);
      }

      if (this.stageAge >= 60) {
         this.advance(2);
      }
   }

   private void tickFire(ServerLevel level) {
      if (this.stageAge == 1) {
         this.fire(level);
      }

      if (this.stageAge >= 16) {
         this.advance(3);
      }
   }

   private void tickAfter(ServerLevel level) {
      if (this.stageAge <= 24) {
         SunFx.aftermath(level, this.position(), (double)this.radius(), this.stageAge);
      }

      if (this.stageAge >= 70) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void scorch(ServerLevel level) {
      Entity caster = this.caster(level);
      float radius = this.radius();

      for (LivingEntity target : this.targets(level, radius)) {
         double distance = this.horizontal(target);
         float falloff = 1.0F - (float)(distance / (double)radius);
         float damage = (float)((double)(this.power * 0.09F * falloff) * (Double)GsConfig.COMMON.damageMultiplier.get());
         if (!(damage <= 0.05F)) {
            target.hurt(GsDamage.sunfall(level, caster), damage);
            target.igniteForSeconds(4.0F);
         }
      }
   }

   private void fire(ServerLevel level) {
      Entity caster = this.caster(level);
      float radius = this.radius();
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity target : this.targets(level, radius)) {
         if (caught >= cap) {
            break;
         }

         caught++;
         double distance = this.horizontal(target);
         float falloff = 1.0F - 0.68F * (float)(distance / (double)radius);
         float damage = (float)((double)(this.power * 4.2F * falloff) * (Double)GsConfig.COMMON.damageMultiplier.get());
         target.invulnerableTime = 0;
         target.hurt(GsDamage.sunfall(level, caster), damage);
         target.igniteForSeconds(10.0F);
         Vec3 out = target.position().subtract(this.position());
         double length = Math.max(0.1, Math.sqrt(out.x * out.x + out.z * out.z));
         target.setDeltaMovement(target.getDeltaMovement().add(out.x / length * 0.9, 0.7, out.z / length * 0.9));
         target.hurtMarked = true;
      }

      SunFx.fired(level, this.position(), (double)radius);
      if (caster instanceof ServerPlayer player) {
         GsAdvancements.sunfell(player, caught);
      }
   }

   private List<LivingEntity> targets(ServerLevel level, float radius) {
      AABB box = new AABB(
         this.getX() - (double)radius,
         this.getY() - 6.0,
         this.getZ() - (double)radius,
         this.getX() + (double)radius,
         this.getY() + 24.0,
         this.getZ() + (double)radius
      );
      Entity caster = this.caster(level);
      return level.getEntitiesOfClass(LivingEntity.class, box, target -> this.affects(target, caster, radius));
   }

   private boolean affects(LivingEntity target, @Nullable Entity caster, float radius) {
      if (target.isAlive() && target != caster && !target.fireImmune()) {
         if (caster != null) {
            if (target.isAlliedTo(caster)) {
               return false;
            }

            if (target instanceof TamableAnimal tame && this.casterId != null && this.casterId.equals(tame.getOwnerUUID())) {
               return false;
            }
         }

         return this.horizontal(target) <= (double)radius;
      } else {
         return false;
      }
   }

   private double horizontal(Entity target) {
      double dx = target.getX() - this.getX();
      double dz = target.getZ() - this.getZ();
      return Math.sqrt(dx * dx + dz * dz);
   }

   @Nullable
   private Entity caster(ServerLevel level) {
      return this.casterId == null ? null : level.getEntity(this.casterId);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (this.level().isClientSide && DATA_STAGE.equals(key)) {
         this.stageAge = this.entityData.get(DATA_STAGE_AGE);
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(1);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 30.0F);
      this.entityData.set(DATA_ANGLE, tag.getFloat("SunAngle"));
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("SunAngle", this.startAngle());
      tag.putFloat("Power", this.power);
      if (this.casterId != null) {
         tag.putUUID("Caster", this.casterId);
      }
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
      return distance < 160000.0;
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
