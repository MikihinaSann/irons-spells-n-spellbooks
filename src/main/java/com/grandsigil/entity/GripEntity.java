package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.GripParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.GripFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class GripEntity extends Entity {
   public static final float LIFT = 30.0F;
   public static final float ISLE_LOW = 26.0F;
   public static final float ISLE_HIGH = 62.0F;
   public static final int STAGE_SEIZE = 0;
   public static final int STAGE_RISE = 1;
   public static final int STAGE_HOLD = 2;
   public static final int STAGE_SLAM = 3;
   public static final int SEIZE_TICKS = 45;
   public static final int RISE_TICKS = 45;
   public static final int HOLD_TICKS = 60;
   public static final int SLAM_TICKS = 40;
   public static final int DRIVE_TICKS = 8;
   private static final int MAX_HELD = 20;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(GripEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(GripEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(GripEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 24.0F;
   private boolean counted;
   private int stageAge;
   private int slammed;
   private final Map<UUID, Double> held = new HashMap<>();

   public GripEntity(EntityType<? extends GripEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public GripEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.GRIP.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 30.0F);
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

   public float seized(float partial) {
      if (this.stage() == 0) {
         float t = Mth.clamp(((float)this.stageAge() + partial) / 45.0F, 0.0F, 1.0F);
         return t * t * (3.0F - 2.0F * t);
      } else {
         return this.stage() == 3 ? 1.0F - this.slam(partial) * 0.35F : 1.0F;
      }
   }

   public float risen(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0 -> 0.0F;
         case 1 -> {
            float t = Mth.clamp(age / 45.0F, 0.0F, 1.0F);
            yield t * t * (3.0F - 2.0F * t);
         }
         case 2 -> 1.0F;
         default -> {
            float t = Mth.clamp(age / 8.0F, 0.0F, 1.0F);
            yield 1.0F - t * t * t;
         }
      };
   }

   public float slam(float partial) {
      return this.stage() != 3 ? 0.0F : Mth.clamp(((float)this.stageAge() + partial) / 40.0F, 0.0F, 1.0F);
   }

   public boolean grips(Entity entity) {
      return this.held.containsKey(entity.getUUID());
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("GripEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(8);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            GripParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickSeize(level);
               break;
            case 1:
               this.tickRise(level);
               break;
            case 2:
               this.tickHold(level);
               break;
            default:
               this.tickSlam(level);
         }
      }
   }

   private void tickSeize(ServerLevel level) {
      if (this.stageAge == 1) {
         GripFx.opens(level, this.position(), (double)this.radius());
      }

      if (this.stageAge % 12 == 0) {
         GripFx.arriving(level, this.position(), (double)this.radius(), (float)this.stageAge / 45.0F);
      }

      if (this.stageAge >= 45) {
         this.seize(level);
         this.advance(1);
      }
   }

   private void tickRise(ServerLevel level) {
      this.carry(level);
      if (this.stageAge >= 45) {
         this.advance(2);
      }
   }

   private void tickHold(ServerLevel level) {
      this.carry(level);
      if (this.stageAge % 20 == 0) {
         GripFx.straining(level, this.position(), (double)this.radius(), this.held.size());
      }

      if (this.stageAge >= 60) {
         this.advance(3);
      }
   }

   private void tickSlam(ServerLevel level) {
      if (this.stageAge == 1) {
         GripFx.drives(level, this.position(), (double)this.radius(), this.held.size());
      }

      if (this.stageAge <= 8) {
         this.drive(level);
      } else if (this.stageAge == 9) {
         this.impact(level);
      }

      if (this.stageAge >= 40) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   private void seize(ServerLevel level) {
      Entity caster = this.caster(level);

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster) && this.held.size() < 20) {
            this.held.put(target.getUUID(), target.getY());
            GripFx.takes(level, target);
         }
      }

      GripFx.closed(level, this.position(), this.held.size());
   }

   private void carry(ServerLevel level) {
      float up = this.risen(0.0F);

      for (LivingEntity target : this.list(level)) {
         double from = this.held.getOrDefault(target.getUUID(), target.getY());
         double to = from + (double)(30.0F * up);
         target.setPos(target.getX(), to, target.getZ());
         target.setDeltaMovement(Vec3.ZERO);
         target.fallDistance = 0.0F;
         target.hasImpulse = true;
         target.hurtMarked = true;
         target.setNoGravity(true);
      }
   }

   private void drive(ServerLevel level) {
      float up = this.risen(0.0F);

      for (LivingEntity target : this.list(level)) {
         double from = this.held.getOrDefault(target.getUUID(), target.getY());
         target.setPos(target.getX(), from + (double)(30.0F * up), target.getZ());
         target.setDeltaMovement(0.0, -3.2, 0.0);
         target.fallDistance = 0.0F;
         target.hurtMarked = true;
      }
   }

   private void impact(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();

      for (LivingEntity target : this.list(level)) {
         target.setNoGravity(false);
         target.invulnerableTime = 0;
         target.hurt(GsDamage.voidgrip(level, caster), this.power * 4.8F * scale);
         this.slammed++;
         GripFx.lands(level, target.position());

         for (LivingEntity near : level.getEntitiesOfClass(
            LivingEntity.class, target.getBoundingBox().inflate(4.0), other -> other != target && this.hostile(other, caster)
         )) {
            near.hurt(GsDamage.voidgrip(level, caster), this.power * 1.3F * scale);
         }
      }

      this.held.clear();
      GripFx.crater(level, this.position(), (double)this.radius(), this.slammed);
      if (this.caster(level) instanceof ServerPlayer player) {
         GsAdvancements.gripped(player, this.slammed);
      }
   }

   private List<LivingEntity> list(ServerLevel level) {
      List<LivingEntity> out = new ArrayList<>(this.held.size());
      this.held.keySet().removeIf(id -> {
         Entity found = level.getEntity(id);
         if (found instanceof LivingEntity living && living.isAlive()) {
            out.add(living);
            return false;
         }

         if (found instanceof LivingEntity dead) {
            dead.setNoGravity(false);
         }

         return true;
      });
      return out;
   }

   @Override
   public void remove(RemovalReason reason) {
      this.release();
      if (!this.level().isClientSide && this.level() instanceof ServerLevel server) {
         for (UUID id : this.held.keySet()) {
            if (server.getEntity(id) instanceof LivingEntity living) {
               living.setNoGravity(false);
            }
         }

         this.held.clear();
      }

      super.remove(reason);
   }

   private List<LivingEntity> inside(ServerLevel level) {
      float r = this.radius();
      AABB box = new AABB(
         this.getX() - (double)r, this.getY() - 8.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 20.0, this.getZ() + (double)r
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return entity.isAlive() && dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - this.getY()) <= 20.0;
   }

   private boolean hostile(LivingEntity target, @Nullable Entity caster) {
      if (target.isAlive() && target != caster) {
         if (caster != null) {
            if (target.isAlliedTo(caster)) {
               return false;
            }

            if (target instanceof TamableAnimal tame && this.casterId != null && this.casterId.equals(tame.getOwnerUUID())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
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

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(8);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 30.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.slammed = tag.getInt("Slammed");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      tag.putInt("Slammed", this.slammed);
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
