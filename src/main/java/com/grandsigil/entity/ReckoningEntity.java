package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.ReckoningParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.ReckoningFx;
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
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class ReckoningEntity extends Entity {
   public static final int STAGE_GATHER = 0;
   public static final int STAGE_THROW = 1;
   public static final int STAGE_FIRE = 2;
   public static final int STAGE_STAND = 3;
   public static final int STAGE_FADE = 4;
   public static final int THROW_TICKS = 8;
   public static final int FIRE_TICKS = 14;
   public static final int STAND_TICKS = 140;
   public static final int FADE_TICKS = 50;
   private static final int REACH_UP = 96;
   private static final int REACH_DOWN = 6;
   private static final int BURN_INTERVAL = 10;
   private static final float BLOW = 4.6F;
   private static final float BLOW_RIM = 0.5F;
   private static final float BURN = 0.2F;
   private static final float UNDEAD = 2.5F;
   private static final int MEND_TICKS = 200;
   private static final int CARRY_TICKS = 400;
   private static final int ROOMFUL = 8;
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(ReckoningEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_STAGE_AGE = SynchedEntityData.defineId(ReckoningEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ReckoningEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_CASTER = SynchedEntityData.defineId(ReckoningEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_GATHER = SynchedEntityData.defineId(ReckoningEntity.class, EntityDataSerializers.INT);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private int stageAge;
   private int struck;
   private boolean counted;

   public ReckoningEntity(EntityType<? extends ReckoningEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public ReckoningEntity(Level level, LivingEntity caster, float power, float radius, int gather) {
      this(GsEntities.RECKONING.get(), level);
      this.setPos(caster.getX(), caster.getY(), caster.getZ());
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_GATHER, Math.max(1, gather));
      this.entityData.set(DATA_RADIUS, radius);
      this.entityData.set(DATA_CASTER, caster.getId());
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_STAGE_AGE, 0);
      builder.define(DATA_RADIUS, 12.0F);
      builder.define(DATA_CASTER, -1);
      builder.define(DATA_GATHER, 100);
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

   public int gather() {
      return Math.max(1, this.entityData.get(DATA_GATHER));
   }

   public int casterId() {
      return this.entityData.get(DATA_CASTER);
   }

   public float standing(float partial) {
      float age = (float)this.stageAge() + partial;

      return switch (this.stage()) {
         case 0, 1 -> 0.0F;
         case 2 -> Mth.clamp(age / 4.0F, 0.0F, 1.0F);
         case 3 -> 1.0F;
         default -> {
            float left = 1.0F - Mth.clamp(age / 50.0F, 0.0F, 1.0F);
            yield left * left * (3.0F - 2.0F * left);
         }
      };
   }

   public float closing(float partial) {
      if (this.stage() != 4) {
         return 0.0F;
      } else {
         float t = Mth.clamp(((float)this.stageAge() + partial) / 50.0F, 0.0F, 1.0F);
         return t * t * (3.0F - 2.0F * t);
      }
   }

   public float flare(float partial) {
      if (this.stage() != 2) {
         return 0.0F;
      } else {
         float age = (float)this.stageAge() + partial;
         return Mth.clamp(1.0F - age / 14.0F, 0.0F, 1.0F);
      }
   }

   public void release(Vec3 target) {
      if (this.stage() == 0) {
         this.setPos(target.x, target.y, target.z);
         if (this.level() instanceof ServerLevel level) {
            ReckoningFx.released(level, target);
         }

         this.advance(1);
      }
   }

   public void fizzle() {
      if (this.stage() == 0) {
         if (this.level() instanceof ServerLevel level) {
            ReckoningFx.lost(level, this.position());
         }

         this.discard();
      }
   }

   @Nullable
   public static ReckoningEntity held(ServerLevel level, LivingEntity caster) {
      UUID id = caster.getUUID();

      for (ReckoningEntity found : level.getEntitiesOfClass(ReckoningEntity.class, caster.getBoundingBox().inflate(3.0))) {
         if (found.stage() == 0 && id.equals(found.casterId)) {
            return found;
         }
      }

      return null;
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("ReckoningEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(10);
      }

      if (this.level().isClientSide) {
         this.stageAge++;
         int served = this.entityData.get(DATA_STAGE_AGE);
         if (Math.abs(served - this.stageAge) > 2) {
            this.stageAge = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            ReckoningParticles.ambient(this);
         }
      } else {
         this.stageAge++;
         this.entityData.set(DATA_STAGE_AGE, this.stageAge);
         ServerLevel level = (ServerLevel)this.level();
         switch (this.stage()) {
            case 0:
               this.tickGather(level);
               break;
            case 1:
               this.tickThrow(level);
               break;
            case 2:
               this.tickFire(level);
               break;
            case 3:
               this.tickStand(level);
               break;
            default:
               this.tickFade(level);
         }
      }
   }

   private void tickGather(ServerLevel level) {
      if (this.caster(level) instanceof LivingEntity holder && holder.isAlive()) {
         this.setPos(holder.getX(), holder.getY(), holder.getZ());
         if (this.stageAge == 1) {
            ReckoningFx.begins(level, this.position());
         }

         ReckoningFx.gathering(level, this.position(), Mth.clamp((float)this.stageAge / (float)this.gather(), 0.0F, 1.0F), this.stageAge);
         if (this.stageAge > this.gather() + 40) {
            this.fizzle();
         }

         return;
      }

      this.fizzle();
   }

   private void tickThrow(ServerLevel level) {
      if (this.stageAge >= 8) {
         this.advance(2);
      }
   }

   private void tickFire(ServerLevel level) {
      if (this.stageAge == 1) {
         ReckoningFx.lands(level, this.position(), (double)this.radius());
         this.strike(level);
      }

      if (this.stageAge >= 14) {
         this.advance(3);
      }
   }

   private void tickStand(ServerLevel level) {
      if (this.stageAge % 10 == 0) {
         this.burn(level);
      }

      if (this.stageAge % 20 == 0) {
         ReckoningFx.standing(level, this.position(), (double)this.radius(), (float)this.stageAge / 140.0F);
      }

      if (this.stageAge >= 140) {
         this.advance(4);
      }
   }

   private void tickFade(ServerLevel level) {
      if (this.stageAge == 1) {
         ReckoningFx.closes(level, this.position(), (double)this.radius());
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

   private void strike(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      float reach = this.radius();

      for (LivingEntity target : this.inside(level)) {
         if (!this.hostile(target, caster)) {
            this.bless(target);
         } else if (this.struck < cap) {
            float dx = (float)(target.getX() - this.getX());
            float dz = (float)(target.getZ() - this.getZ());
            float out = Mth.clamp(Mth.sqrt(dx * dx + dz * dz) / reach, 0.0F, 1.0F);
            float falloff = Mth.lerp(out, 1.0F, 0.5F);
            boolean undead = target.getType().is(EntityTypeTags.UNDEAD);
            target.invulnerableTime = 0;
            float damage = this.power * 4.6F * falloff * (undead ? 2.5F : 1.0F) * scale;
            target.hurt(GsDamage.reckoning(level, caster), damage);
            ReckoningFx.judged(level, target);
            this.struck++;
         }
      }

      if (caster instanceof ServerPlayer player) {
         GsAdvancements.reckoned(player, this.struck);
      }
   }

   private void burn(ServerLevel level) {
      Entity caster = this.caster(level);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();

      for (LivingEntity target : this.inside(level)) {
         if (this.hostile(target, caster)) {
            boolean undead = target.getType().is(EntityTypeTags.UNDEAD);
            target.invulnerableTime = 0;
            target.hurt(GsDamage.reckoning(level, caster), this.power * 0.2F * (undead ? 2.5F : 1.0F) * scale);
         }
      }
   }

   private void bless(LivingEntity ally) {
      ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, false, true));
      ally.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 2, false, false, true));
   }

   private List<LivingEntity> inside(ServerLevel level) {
      float r = this.radius();
      AABB box = new AABB(
         this.getX() - (double)r, this.getY() - 6.0, this.getZ() - (double)r, this.getX() + (double)r, this.getY() + 96.0, this.getZ() + (double)r
      );
      return level.getEntitiesOfClass(LivingEntity.class, box, this::covers);
   }

   public boolean covers(Entity entity) {
      double r = (double)this.radius();
      double dx = entity.getX() - this.getX();
      double dz = entity.getZ() - this.getZ();
      double dy = entity.getY() - this.getY();
      return dx * dx + dz * dz <= r * r && dy >= -6.0 && dy <= 96.0;
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
   public void remove(RemovalReason reason) {
      this.release();
      super.remove(reason);
   }

   private void release() {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(10);
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (this.level().isClientSide && DATA_STAGE.equals(key)) {
         this.stageAge = this.entityData.get(DATA_STAGE_AGE);
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag tag) {
      this.entityData.set(DATA_STAGE, tag.getInt("Stage"));
      this.entityData.set(DATA_RADIUS, tag.contains("Radius") ? tag.getFloat("Radius") : 12.0F);
      this.stageAge = tag.getInt("StageAge");
      this.entityData.set(DATA_STAGE_AGE, this.stageAge);
      this.power = tag.getFloat("Power");
      this.entityData.set(DATA_GATHER, tag.contains("Gather") ? tag.getInt("Gather") : 100);
      this.struck = tag.getInt("Struck");
      this.casterId = tag.hasUUID("Caster") ? tag.getUUID("Caster") : null;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putInt("Stage", this.stage());
      tag.putInt("StageAge", this.stageAge);
      tag.putFloat("Radius", this.radius());
      tag.putFloat("Power", this.power);
      tag.putInt("Gather", this.gather());
      tag.putInt("Struck", this.struck);
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
      return distance < 262144.0;
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
