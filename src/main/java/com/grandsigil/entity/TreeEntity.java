package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.TreeParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.TreeFx;
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

public class TreeEntity extends Entity {
   public static final int MAX_GENERATION = 6;
   public static final int STAGE_GROW = 0;
   public static final int STAGE_STAND = 1;
   public static final int STAGE_BLOOM = 2;
   public static final int GROW_TICKS = 40;
   public static final int STAND_TICKS = 300;
   public static final int BLOOM_TICKS = 50;
   private static final int PULSE_INTERVAL = 10;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_GENERATION = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(TreeEntity.class, EntityDataSerializers.FLOAT);
   @Nullable
   private UUID casterId;
   private float power = 16.0F;
   private boolean counted;
   private int stageAge;
   private static int standing;

   public TreeEntity(EntityType<? extends TreeEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public TreeEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      this(GsEntities.TREE.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_RADIUS, radius);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_GENERATION, 0);
      builder.define(DATA_RADIUS, 36.0F);
   }

   public int stage() {
      return this.entityData.get(DATA_STAGE);
   }

   public int stageAge() {
      return this.stageAge;
   }

   public int generation() {
      return this.entityData.get(DATA_GENERATION);
   }

   public static boolean anyStanding() {
      return standing > 0;
   }

   public float radius() {
      return this.entityData.get(DATA_RADIUS);
   }

   public float height() {
      return 34.0F + 5.0F * (float)this.generation();
   }

   public float rootReach(float partial) {
      float elapsed = switch (this.stage()) {
         case 0 -> (float)this.stageAge() + partial;
         case 1 -> (float)(40 + this.stageAge()) + partial;
         default -> 340.0F;
      };
      float total = 340.0F;
      return Mth.clamp(0.22F + 0.78F * (elapsed / total) + 0.04F * (float)this.generation(), 0.0F, 1.0F);
   }

   public float risen(float partial) {
      if (this.stage() != 0) {
         return 1.0F;
      } else {
         float t = Mth.clamp(((float)this.stageAge() + partial) / 40.0F, 0.0F, 1.0F);
         return t * t * (3.0F - 2.0F * t);
      }
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("TreeEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(3);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            TreeParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickGrow(level);
               break;
            case 1:
               this.tickStand(level);
               break;
            default:
               this.tickBloom(level);
         }
      }
   }

   private void tickGrow(ServerLevel level) {
      if (this.stageAge == 1) {
         TreeFx.erupts(level, this.position(), (double)this.radius());
      }

      if (this.stageAge >= 40) {
         this.advance(1);
      }
   }

   private void tickStand(ServerLevel level) {
      this.hold(level);
      if (this.stageAge % 10 == 0) {
         this.pulse(level);
      }

      if (this.stageAge % 40 == 0) {
         TreeFx.creaking(level, this.position(), (double)this.radius(), this.generation());
      }

      if (this.stageAge >= 300) {
         this.advance(2);
      }
   }

   private void tickBloom(ServerLevel level) {
      if (this.stageAge == 1) {
         this.bloom(level);
      }

      if (this.stageAge <= 26) {
         TreeFx.spores(level, this.position(), (double)this.radius(), this.stageAge, this.generation());
      }

      if (this.stageAge >= 50) {
         this.discard();
      }
   }

   private void advance(int stage) {
      this.entityData.set(DATA_STAGE, stage);
      this.stageAge = 0;
      this.entityData.set(DATA_STAGE_AGE, 0);
   }

   public boolean feed(ServerLevel level) {
      if (this.stage() == 1 && this.generation() < 6) {
         this.entityData.set(DATA_GENERATION, this.generation() + 1);
         TreeFx.fed(level, this.position(), (double)this.radius(), this.generation());
         return true;
      } else {
         return false;
      }
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      return dx * dx + dz * dz <= r * r && Math.abs(entity.getY() - this.getY()) <= 12.0;
   }

   private void hold(ServerLevel level) {
      Entity caster = this.caster(level);

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster)) {
            Vec3 motion = target.getDeltaMovement();
            target.setDeltaMovement(motion.x * 0.55, motion.y, motion.z * 0.55);
            target.hasImpulse = true;
            target.hurtMarked = true;
         }
      }
   }

   private void pulse(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster)) {
            target.hurt(GsDamage.worldTree(level, caster), this.power * 0.13F * scale);
            TreeFx.bled(level, target);
         } else if (target.getHealth() < target.getMaxHealth()) {
            target.heal(this.power * 0.08F);
            TreeFx.mended(level, target);
         }
      }
   }

   private void bloom(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float burst = this.power * (1.2F + 0.5F * (float)this.generation()) * scale;
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int hit = 0;

      for (LivingEntity target : this.inside(level)) {
         if (hit >= cap) {
            break;
         }

         if (this.hostile(target, caster)) {
            hit++;
            target.hurt(GsDamage.worldTree(level, caster), burst);
         } else {
            target.heal(this.power * (0.7F + 0.35F * (float)this.generation()));
         }
      }

      TreeFx.blooms(level, this.position(), (double)this.radius(), this.generation());
      if (caster instanceof ServerPlayer player) {
         GsAdvancements.treeBloomed(player, this.generation(), 6);
      }
   }

   private List<LivingEntity> inside(ServerLevel level) {
      float r = this.radius();
      AABB box = new AABB(
         this.getX() - (double)r, this.getY() - 6.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 20.0, this.getZ() + (double)r
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
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

   public boolean plantedBy(Entity entity) {
      return this.casterId != null && this.casterId.equals(entity.getUUID());
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
         GsPresence.leave(3);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_GENERATION, tag.getInt("Generation"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 36.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("Generation", this.generation());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
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
      return distance < 90000.0;
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
