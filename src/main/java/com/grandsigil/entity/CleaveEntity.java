package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.CleaveParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.CleaveFx;
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

public class CleaveEntity extends Entity {
   public static final int FOCUS = 20;
   public static final int SWEEP = 3;
   public static final int OPEN = 12;
   public static final int HOLD = 48;
   public static final int CLOSE = 8;
   public static final int FADE = 30;
   public static final int T_CUT = 20;
   public static final int T_OPEN = 23;
   public static final int T_HOLD = 35;
   public static final int T_CLOSE = 83;
   public static final int T_SHUT = 91;
   public static final int LIFE = 121;
   public static final float HALF = 50.0F;
   public static final float RISE = 11.0F;
   public static final float SLANT = 0.26F;
   public static final float WIDTH = 16.0F;
   public static final float CUT_REACH = 6.0F;
   public static final float PULL = 18.0F;
   public static final float CUT_SHARE = 3.0F;
   public static final float TEAR_SHARE = 0.12F;
   public static final float SNAP_SHARE = 1.2F;
   private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(CleaveEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_HEADING = SynchedEntityData.defineId(CleaveEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_SEED = SynchedEntityData.defineId(CleaveEntity.class, EntityDataSerializers.INT);
   @Nullable
   private UUID casterId;
   private float power = 22.0F;
   private int age;
   private int kills;
   private boolean counted;
   private final Set<UUID> dead = new HashSet<>();

   public CleaveEntity(EntityType<? extends CleaveEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public CleaveEntity(Level level, LivingEntity caster, Vec3 centre, float heading, float power) {
      this(GsEntities.DIMENSION_CUT.get(), level);
      this.setPos(centre.x, centre.y, centre.z);
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_HEADING, heading);
      this.entityData.set(DATA_SEED, level.random.nextInt());
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_AGE, 0);
      builder.define(DATA_HEADING, 0.0F);
      builder.define(DATA_SEED, 0);
   }

   public float age(float partial) {
      return (float)this.age + partial;
   }

   public float heading() {
      return this.entityData.get(DATA_HEADING);
   }

   public int seed() {
      return this.entityData.get(DATA_SEED);
   }

   public static Vec3 along(float heading, float s) {
      return new Vec3(Math.cos((double)heading) * 50.0 * (double)s, 11.0 + Math.tan(0.26F) * 50.0 * (double)s, Math.sin((double)heading) * 50.0 * (double)s);
   }

   public static float open(float t) {
      if (t < 23.0F) {
         return 0.0F;
      } else if (t < 83.0F) {
         float u = Mth.clamp((t - 23.0F) / 12.0F, 0.0F, 1.0F);
         float ease = 1.0F - (1.0F - u) * (1.0F - u) * (1.0F - u);
         return ease * (1.0F + 0.05F * Mth.sin((t - 23.0F) * 0.25F));
      } else {
         float u = Mth.clamp((t - 83.0F) / 8.0F, 0.0F, 1.0F);
         return 1.0F - u * u * u;
      }
   }

   public static float profile(float s) {
      return (float)Math.pow((double)Math.max(0.0F, 1.0F - s * s), 0.7);
   }

   public static float presence(float t) {
      return t < 91.0F ? smooth(t / 20.0F) : 1.0F - smooth((t - 91.0F) / 30.0F);
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
         GsWatchdog.end("CleaveEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      this.age++;
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(19);
      }

      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_AGE);
         if (Math.abs(served - this.age) > 2) {
            this.age = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            CleaveParticles.tick(this);
         }
      } else {
         this.entityData.set(DATA_AGE, this.age);
         ServerLevel level = (ServerLevel)this.level();
         Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
         if (this.age == 1) {
            CleaveFx.focuses(level, this.position().add(0.0, 11.0, 0.0));
         }

         if (this.age == 20) {
            CleaveFx.cuts(level, this.position().add(0.0, 11.0, 0.0));
         }

         if (this.age == 22) {
            this.cut(level, caster);
         }

         if (this.age == 25) {
            CleaveFx.tears(level, this.position().add(0.0, 11.0, 0.0));
         }

         if (this.age > 23 && this.age < 83) {
            if (this.age % 2 == 0) {
               this.pull(level, caster);
            }

            if (this.age % 10 == 0) {
               this.tear(level, caster);
            }

            if (this.age % 30 == 0) {
               CleaveFx.hums(level, this.position().add(0.0, 11.0, 0.0));
            }
         }

         if (this.age == 91) {
            this.snap(level, caster);
         }

         if (this.age == 93 && caster instanceof ServerPlayer player) {
            GsAdvancements.cleaved(player, this.kills);
         }

         if (this.age >= 121) {
            this.discard();
         }
      }
   }

   private double[] nearest(Vec3 p) {
      Vec3 a = this.position().add(along(this.heading(), -1.0F));
      Vec3 b = this.position().add(along(this.heading(), 1.0F));
      Vec3 ab = b.subtract(a);
      double u = Mth.clamp(p.subtract(a).dot(ab) / ab.lengthSqr(), 0.0, 1.0);
      Vec3 q = a.add(ab.scale(u));
      return new double[]{u * 2.0 - 1.0, p.distanceTo(q), q.x, q.y, q.z};
   }

   private AABB box(double grow) {
      Vec3 a = this.position().add(along(this.heading(), -1.0F));
      Vec3 b = this.position().add(along(this.heading(), 1.0F));
      return new AABB(a, b).inflate(grow);
   }

   private void cut(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      int caught = 0;

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(8.0), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         if (caught >= cap) {
            break;
         }

         double[] n = this.nearest(e.getBoundingBox().getCenter());
         if (!(n[1] > 6.0 + (double)e.getBbWidth() * 0.5)) {
            caught++;
            e.invulnerableTime = 0;
            this.hurt(level, caster, e, this.power * 3.0F * scale);
         }
      }
   }

   private void pull(ServerLevel level, @Nullable Entity caster) {
      float k = open((float)this.age);

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(18.0), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         Vec3 c = e.getBoundingBox().getCenter();
         double[] n = this.nearest(c);
         if (!(n[1] > 18.0) && !(n[1] < 0.8)) {
            Vec3 in = new Vec3(n[2], n[3], n[4]).subtract(c).normalize();
            double strength = 0.12 * (double)k * (1.0 - n[1] / 18.0 * 0.6);
            e.setDeltaMovement(e.getDeltaMovement().scale(0.8).add(in.scale(strength)));
            e.hurtMarked = true;
            e.fallDistance = 0.0F;
         }
      }
   }

   private void tear(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(16.0), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         double[] n = this.nearest(e.getBoundingBox().getCenter());
         float mouth = 8.0F * profile((float)n[0]) * open((float)this.age) + 2.0F;
         if (!(n[1] > (double)mouth)) {
            e.invulnerableTime = 0;
            this.hurt(level, caster, e, this.power * 0.12F * scale);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, true));
         }
      }
   }

   private void snap(ServerLevel level, @Nullable Entity caster) {
      CleaveFx.snaps(level, this.position().add(0.0, 11.0, 0.0));
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.box(10.0), t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         Vec3 c = e.getBoundingBox().getCenter();
         double[] n = this.nearest(c);
         if (!(n[1] > 10.0)) {
            e.invulnerableTime = 0;
            this.hurt(level, caster, e, this.power * 1.2F * scale * (float)(1.0 - 0.4 * n[1] / 10.0));
            Vec3 out = c.subtract(n[2], c.y, n[4]);
            out = out.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : out.normalize();
            e.push(out.x * 1.3, 0.5, out.z * 1.3);
            e.hurtMarked = true;
         }
      }
   }

   private void hurt(ServerLevel level, @Nullable Entity caster, LivingEntity target, float amount) {
      boolean alive = target.isAlive();
      target.hurt(GsDamage.dimensionCut(level, (Entity)(caster == null ? this : caster)), amount);
      if (alive && !target.isAlive() && this.dead.add(target.getUUID())) {
         this.kills++;
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(19);
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
