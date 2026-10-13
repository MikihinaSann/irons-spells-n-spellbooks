package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.IceAgeParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.IceAgeFx;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class IceAgeEntity extends Entity {
   public static final int GATHER = 40;
   public static final int STORM = 180;
   public static final int ZERO = 24;
   public static final int FADE = 56;
   public static final int T_STORM = 40;
   public static final int T_ZERO = 220;
   public static final int T_SHATTER = 244;
   public static final int LIFE = 300;
   public static final float FIELD = 56.0F;
   public static final float HEIGHT = 70.0F;
   public static final float CROWN = 58.0F;
   public static final int SHARDS = 28;
   public static final int SHARD_EVERY = 6;
   public static final int SHARD_FALL = 10;
   public static final float SHARD_BURST = 6.5F;
   public static final float CHILL_SHARE = 0.08F;
   public static final float SHARD_SHARE = 0.55F;
   public static final float SHATTER_SHARE = 1.6F;
   public static final float FROZEN_BONUS = 1.35F;
   private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(IceAgeEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_SEED = SynchedEntityData.defineId(IceAgeEntity.class, EntityDataSerializers.INT);
   @Nullable
   private UUID casterId;
   private float power = 22.0F;
   private int age;
   private int kills;
   private boolean counted;
   private final Set<UUID> dead = new HashSet<>();

   public IceAgeEntity(EntityType<? extends IceAgeEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public IceAgeEntity(Level level, LivingEntity caster, Vec3 at, float power) {
      this(GsEntities.ICE_AGE.get(), level);
      this.setPos(at.x, at.y, at.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_SEED, level.random.nextInt());
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_AGE, 0);
      builder.define(DATA_SEED, 0);
   }

   public float age(float partial) {
      return (float)this.age + partial;
   }

   public int seed() {
      return this.entityData.get(DATA_SEED);
   }

   public static float sky(float t) {
      return t < 244.0F ? smooth(t / 40.0F) : 1.0F - smooth((t - 244.0F - 6.0F) / 46.0F);
   }

   public static float descent(float t) {
      return smooth((t - 10.0F) / 46.0F);
   }

   public static float zero(float t) {
      return smooth((t - 220.0F) / 24.0F);
   }

   public static float vortex(float t) {
      return t >= 244.0F ? 0.0F : descent(t);
   }

   public static float frost(float t) {
      return smooth((t - 6.0F) / 70.0F);
   }

   public static float frostAlpha(float t) {
      return t < 244.0F ? 1.0F : 1.0F - smooth((t - 244.0F - 10.0F) / 44.0F);
   }

   public static float smooth(float u) {
      u = Mth.clamp(u, 0.0F, 1.0F);
      return u * u * (3.0F - 2.0F * u);
   }

   public static int shardStart(int i) {
      return 48 + i * 6;
   }

   public static Vec3 shardTarget(int seed, int i) {
      RandomSource random = RandomSource.create((long)seed * 31L + (long)i * 7919L);
      double a = random.nextDouble() * Math.PI * 2.0;
      double r = 56.0 * (0.12 + 0.8 * Math.sqrt(random.nextDouble()));
      return new Vec3(Math.cos(a) * r, 0.0, Math.sin(a) * r);
   }

   public static Vec3 shardOrigin(int seed, int i) {
      Vec3 to = shardTarget(seed, i);
      double a = Math.atan2(to.z, to.x) - 0.7;
      double r = Math.min(46.400000000000006, to.horizontalDistance() * 0.6 + 12.0);
      return new Vec3(Math.cos(a) * r, 72.0, Math.sin(a) * r);
   }

   @Override
   public void tick() {
      long started = GsWatchdog.start();

      try {
         this.watchedTick();
      } finally {
         GsWatchdog.end("IceAgeEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      this.age++;
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(17);
      }

      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_AGE);
         if (Math.abs(served - this.age) > 2) {
            this.age = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            IceAgeParticles.tick(this);
         }
      } else {
         this.entityData.set(DATA_AGE, this.age);
         ServerLevel level = (ServerLevel)this.level();
         Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
         if (this.age == 1) {
            IceAgeFx.gathers(level, this.position());
         }

         if (this.age == 40) {
            IceAgeFx.descends(level, this.position());
         }

         if (this.age >= 40 && this.age < 220 && (this.age - 40) % 40 == 0) {
            IceAgeFx.howls(level, this.position(), this.age);
         }

         if (this.age >= 40 && this.age < 244 && this.age % 10 == 0) {
            this.chill(level, caster);
         }

         for (int i = 0; i < 28; i++) {
            if (this.age == shardStart(i) + 10) {
               this.shard(level, caster, i);
            }
         }

         if (this.age == 220) {
            IceAgeFx.stills(level, this.position());
         }

         if (this.age > 220 && this.age < 244) {
            this.draw(level, caster);
         }

         if (this.age == 244) {
            this.shatter(level, caster);
         }

         if (this.age == 246 && caster instanceof ServerPlayer player) {
            GsAdvancements.froze(player, this.kills);
         }

         if (this.age >= 300) {
            this.discard();
         }
      }
   }

   private AABB box(float radius) {
      return new AABB(this.position(), this.position()).inflate((double)radius, 0.0, (double)radius).expandTowards(0.0, 70.0, 0.0).move(0.0, -10.0, 0.0);
   }

   private boolean inField(Entity e, float radius) {
      double dx = e.getX() - this.getX();
      double dz = e.getZ() - this.getZ();
      return dx * dx + dz * dz <= (double)(radius * radius);
   }

   private void chill(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      float reach = 56.0F * Math.max(0.3F, frost((float)this.age));

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(reach), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         if (this.inField(e, reach)) {
            this.hurt(level, caster, e, this.power * 0.08F * scale);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, true));
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, true));
            e.setTicksFrozen(Math.min(e.getTicksRequiredToFreeze() + 60, e.getTicksFrozen() + 50));
         }
      }
   }

   private void shard(ServerLevel level, @Nullable Entity caster, int i) {
      Vec3 off = shardTarget(this.seed(), i);
      int gx = Mth.floor(this.getX() + off.x);
      int gz = Mth.floor(this.getZ() + off.z);
      double gy = (double)level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, gx, gz);
      if (Math.abs(gy - this.getY()) > 24.0) {
         gy = this.getY();
      }

      Vec3 at = new Vec3(this.getX() + off.x, gy, this.getZ() + off.z);
      IceAgeFx.lands(level, at);
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      AABB box = new AABB(at, at).inflate(6.5, 3.9, 6.5);

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         double d = e.position().distanceTo(at);
         if (!(d > 6.5)) {
            e.invulnerableTime = 0;
            this.hurt(level, caster, e, this.power * 0.55F * scale * (float)(1.0 - 0.5 * d / 6.5));
            e.setTicksFrozen(Math.min(e.getTicksRequiredToFreeze() + 60, e.getTicksFrozen() + 80));
         }
      }
   }

   private void draw(ServerLevel level, @Nullable Entity caster) {
      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(56.0F), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         if (this.inField(e, 56.0F)) {
            Vec3 in = new Vec3(this.getX() - e.getX(), 0.0, this.getZ() - e.getZ());
            double d = in.length();
            if (d > 3.0) {
               in = in.scale(Math.min(0.35, d * 0.012) / d);
               e.setDeltaMovement(e.getDeltaMovement().scale(0.5).add(in.x, 0.0, in.z));
               e.hurtMarked = true;
            }

            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 4, false, true));
            e.setTicksFrozen(Math.max(e.getTicksFrozen(), e.getTicksRequiredToFreeze() + 40));
         }
      }
   }

   private void shatter(ServerLevel level, @Nullable Entity caster) {
      IceAgeFx.shatters(level, this.position());
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(64.4F), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         double dx = e.getX() - this.getX();
         double dz = e.getZ() - this.getZ();
         double d = Math.sqrt(dx * dx + dz * dz);
         if (!(d > 64.39999999999999) && caught < cap) {
            caught++;
            float falloff = d <= 33.6 ? 1.0F : (float)Mth.clampedLerp(1.0, 0.5, (d - 33.6) / 30.800000000000004);
            float bonus = e.isFullyFrozen() ? 1.35F : 1.0F;
            e.invulnerableTime = 0;
            this.hurt(level, caster, e, this.power * 1.6F * scale * falloff * bonus);
            e.setTicksFrozen(0);
            Vec3 out = new Vec3(dx, 0.0, dz);
            out = out.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : out.normalize();
            e.push(out.x * 1.2, 0.5, out.z * 1.2);
            e.hurtMarked = true;
         }
      }
   }

   private void hurt(ServerLevel level, @Nullable Entity caster, LivingEntity target, float amount) {
      boolean alive = target.isAlive();
      target.hurt(GsDamage.iceAge(level, (Entity)(caster == null ? this : caster)), amount);
      if (alive && !target.isAlive() && this.dead.add(target.getUUID())) {
         this.kills++;
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(17);
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
