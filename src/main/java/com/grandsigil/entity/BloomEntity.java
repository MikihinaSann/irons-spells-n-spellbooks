package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.BloomParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.BloomFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.HashSet;
import java.util.Set;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class BloomEntity extends Entity {
   public static final int SPROUT = 30;
   public static final int RISE = 30;
   public static final int OPEN = 30;
   public static final int PEAK = 180;
   public static final int FADE = 50;
   public static final int T_RISE = 30;
   public static final int T_OPEN = 60;
   public static final int T_PEAK = 90;
   public static final int T_FADE = 270;
   public static final int LIFE = 320;
   public static final float FIELD = 48.0F;
   public static final float STEM = 46.0F;
   public static final int WAVE_EVERY = 40;
   public static final float BURST = 30.0F;
   public static final float WILT_SHARE = 0.1F;
   public static final float WAVE_SHARE = 0.35F;
   public static final float BURST_SHARE = 1.4F;
   public static final float HEAL_SHARE = 0.3F;
   private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(BloomEntity.class, EntityDataSerializers.INT);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private int age;
   private int kills;
   private boolean counted;
   private final Set<UUID> dead = new HashSet<>();

   public BloomEntity(EntityType<? extends BloomEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public BloomEntity(Level level, LivingEntity caster, Vec3 at, float power) {
      this(GsEntities.FULL_BLOOM.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_AGE, 0);
   }

   public float age(float partial) {
      return (float)this.age + partial;
   }

   public static float growth(float t) {
      return t < 30.0F ? 0.12F * smooth(t / 30.0F) : 0.12F + 0.88F * smooth(Mth.clamp((t - 30.0F) / 30.0F, 0.0F, 1.0F));
   }

   public static float open(float t) {
      if (t < 60.0F) {
         return 0.0F;
      } else {
         float u = Mth.clamp((t - 60.0F) / 30.0F, 0.0F, 1.0F);
         float c = 1.4F;
         float v = u - 1.0F;
         return 1.0F + (c + 1.0F) * v * v * v + c * v * v;
      }
   }

   public static float fade(float t) {
      return Mth.clamp((t - 270.0F) / 50.0F, 0.0F, 1.0F);
   }

   public static float field(float t) {
      if (t < 60.0F) {
         return Mth.clamp(t / 60.0F, 0.0F, 1.0F) * 0.4F;
      } else {
         return t < 270.0F ? 0.4F + 0.6F * Mth.clamp((t - 60.0F) / 30.0F, 0.0F, 1.0F) : 1.0F - fade(t);
      }
   }

   public static float smooth(float u) {
      u = Mth.clamp(u, 0.0F, 1.0F);
      return u * u * (3.0F - 2.0F * u);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("BloomEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      this.age++;
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(16);
      }

      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_AGE);
         if (Math.abs(served - this.age) > 2) {
            this.age = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            BloomParticles.tick(this);
         }
      } else {
         this.entityData.set(DATA_AGE, this.age);
         ServerLevel level = (ServerLevel)this.level();
         Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
         Vec3 top = this.position().add(0.0, 46.0, 0.0);
         if (this.age == 1) {
            BloomFx.sprouts(level, this.position());
         }

         if (this.age == 30) {
            BloomFx.rises(level, this.position());
         }

         if (this.age == 60) {
            BloomFx.opens(level, top);
         }

         if (this.age >= 60 && this.age < 270 && this.age % 10 == 0) {
            this.wilt(level, caster);
         }

         if (this.age >= 90 && this.age < 270 && (this.age - 90) % 40 == 0) {
            this.wave(level, caster);
         }

         if (this.age >= 90 && this.age < 270 && this.age % 30 == 0) {
            BloomFx.hums(level, top, this.age);
         }

         if (this.age == 270) {
            this.burst(level, caster);
         }

         if (this.age == 272 && caster instanceof ServerPlayer player) {
            GsAdvancements.bloomed(player, this.kills);
         }

         if (this.age >= 320) {
            this.discard();
         }
      }
   }

   private void wilt(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      AABB box = new AABB(this.position(), this.position()).inflate(48.0, 0.0, 48.0).expandTowards(0.0, 56.0, 0.0).move(0.0, -8.0, 0.0);

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
         double dx = e.getX() - this.getX();
         double dz = e.getZ() - this.getZ();
         if (!(dx * dx + dz * dz > 2304.0)) {
            if (ArsenalEntity.hostile(e, caster, this.casterId)) {
               this.hurt(level, caster, e, this.power * 0.1F * scale);
               e.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1, false, true));
               e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, true));
            } else if (e == caster || caster != null && e.isAlliedTo(caster)) {
               e.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, true));
            }
         }
      }
   }

   private void wave(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      BloomFx.wave(level, this.position());
      AABB box = new AABB(this.position(), this.position()).inflate(48.0, 0.0, 48.0).expandTowards(0.0, 56.0, 0.0).move(0.0, -8.0, 0.0);
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         double dx = e.getX() - this.getX();
         double dz = e.getZ() - this.getZ();
         if (!(dx * dx + dz * dz > 2304.0) && caught < cap) {
            caught++;
            e.invulnerableTime = 0;
            this.hurt(level, caster, e, this.power * 0.35F * scale);
            Vec3 out = new Vec3(dx, 0.0, dz);
            out = out.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : out.normalize();
            e.push(out.x * 0.6, 0.25, out.z * 0.6);
            e.hurtMarked = true;
         }
      }
   }

   private void burst(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      BloomFx.bursts(level, this.position().add(0.0, 46.0, 0.0), this.position());
      AABB box = new AABB(this.position(), this.position()).inflate(30.0, 0.0, 30.0).expandTowards(0.0, 56.0, 0.0).move(0.0, -8.0, 0.0);

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
         double dx = e.getX() - this.getX();
         double dz = e.getZ() - this.getZ();
         double d = Math.sqrt(dx * dx + dz * dz);
         if (!(d > 42.0)) {
            float falloff = d <= 30.0 ? 1.0F : (float)(1.0 - (d - 30.0) / 12.0);
            if (ArsenalEntity.hostile(e, caster, this.casterId)) {
               e.invulnerableTime = 0;
               this.hurt(level, caster, e, this.power * 1.4F * scale * falloff);
            } else if (e == caster || caster != null && e.isAlliedTo(caster)) {
               e.heal(this.power * 0.3F);
            }
         }
      }
   }

   private void hurt(ServerLevel level, @Nullable Entity caster, LivingEntity target, float amount) {
      boolean alive = target.isAlive();
      target.hurt(GsDamage.fullBloom(level, (Entity)(caster == null ? this : caster)), amount);
      if (alive && !target.isAlive() && this.dead.add(target.getUUID())) {
         this.kills++;
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(16);
      }

      super.remove(reason);
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
   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 102400.0;
   }
}
