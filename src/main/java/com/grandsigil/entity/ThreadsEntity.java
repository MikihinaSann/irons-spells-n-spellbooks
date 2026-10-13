package com.grandsigil.entity;

import com.grandsigil.GsPresence;
import com.grandsigil.GsWatchdog;
import com.grandsigil.client.ThreadsParticles;
import com.grandsigil.config.GsConfig;
import com.grandsigil.event.GsAdvancements;
import com.grandsigil.fx.ThreadsFx;
import com.grandsigil.registry.GsDamage;
import com.grandsigil.registry.GsEntities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
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

public class ThreadsEntity extends Entity {
   public static final int RISE = 30;
   public static final int BIND = 16;
   public static final int DRAIN = 160;
   public static final int COLLAPSE = 24;
   public static final int FADE = 36;
   public static final int T_BIND = 30;
   public static final int T_DRAIN = 46;
   public static final int T_SNAP = 206;
   public static final int T_END = 230;
   public static final int LIFE = 266;
   public static final int BEAT = 20;
   public static final float REACH = 40.0F;
   public static final float HEIGHT = 34.0F;
   public static final float DRAIN_SHARE = 0.15F;
   public static final float CROWD_BONUS = 0.04F;
   public static final int CROWD = 15;
   public static final float HEAL_RATIO = 0.35F;
   public static final float SNAP_SHARE = 1.2F;
   public static final float SNAP_RETURN = 0.3F;
   private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(ThreadsEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_CASTER = SynchedEntityData.defineId(ThreadsEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<CompoundTag> DATA_BOUND = SynchedEntityData.defineId(ThreadsEntity.class, EntityDataSerializers.COMPOUND_TAG);
   @Nullable
   private UUID casterId;
   private float power = 20.0F;
   private int age;
   private int kills;
   private boolean counted;
   private float drunk;
   private final Set<UUID> dead = new HashSet<>();
   private final Map<Integer, Integer> boundAt = new LinkedHashMap<>();
   private final Map<Integer, Float> given = new HashMap<>();

   public ThreadsEntity(EntityType<? extends ThreadsEntity> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public ThreadsEntity(Level level, LivingEntity caster, float power) {
      this(GsEntities.CRIMSON_THREADS.get(), level);
      this.setPos(caster.getX(), caster.getY(), caster.getZ());
      this.casterId = caster.getUUID();
      this.power = power;
      this.entityData.set(DATA_CASTER, caster.getId());
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_AGE, 0);
      builder.define(DATA_CASTER, -1);
      builder.define(DATA_BOUND, new CompoundTag());
   }

   public float age(float partial) {
      return (float)this.age + partial;
   }

   public int casterEntityId() {
      return this.entityData.get(DATA_CASTER);
   }

   public int[] boundIds() {
      return this.entityData.get(DATA_BOUND).getIntArray("ids");
   }

   public int[] boundTicks() {
      return this.entityData.get(DATA_BOUND).getIntArray("at");
   }

   public static float beat(float t) {
      float ph = t % 20.0F / 20.0F;
      float a = (float)Math.exp(-Math.pow((double)ph / 0.06, 2.0)) + (float)Math.exp(-Math.pow((double)(ph - 1.0F) / 0.06, 2.0));
      float b = 0.6F * (float)Math.exp(-Math.pow((double)(ph - 0.2F) / 0.06, 2.0));
      return Math.min(1.0F, a + b);
   }

   public static float heart(float t) {
      if (t < 206.0F) {
         float gather = smooth(t / 30.0F);
         float swell = smooth((t - 46.0F) / 160.0F);
         return 9.0F * gather + 5.0F * swell;
      } else {
         return 14.0F * (1.0F - smooth((t - 206.0F) / 24.0F)) + 0.3F;
      }
   }

   public static float descent(float t) {
      float u = Mth.clamp((t - 206.0F) / 24.0F, 0.0F, 1.0F);
      return u * u;
   }

   public static float presence(float t) {
      return t < 230.0F ? smooth(t / 30.0F) : 1.0F - smooth((t - 230.0F) / 36.0F);
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
         GsWatchdog.end("ThreadsEntity.tick", started);
      }
   }

   private void watchedTick() {
      super.tick();
      this.age++;
      if (!this.counted) {
         this.counted = true;
         GsPresence.enter(18);
      }

      if (this.level().isClientSide) {
         int served = this.entityData.get(DATA_AGE);
         if (Math.abs(served - this.age) > 2) {
            this.age = served;
         }

         if (FMLEnvironment.dist.isClient()) {
            ThreadsParticles.tick(this);
         }
      } else {
         this.entityData.set(DATA_AGE, this.age);
         ServerLevel level = (ServerLevel)this.level();
         Entity caster = this.casterId == null ? null : level.getEntity(this.casterId);
         if (caster != null && caster.getId() != this.entityData.get(DATA_CASTER)) {
            this.entityData.set(DATA_CASTER, caster.getId());
         }

         Vec3 heart = this.position().add(0.0, 34.0, 0.0);
         if (this.age == 1) {
            ThreadsFx.rises(level, this.position());
         }

         if (this.age >= 15 && this.age < 206 && this.age % 20 == 0) {
            ThreadsFx.beats(level, heart, this.boundAt.size());
         }

         if (this.age == 30 || this.age > 30 && this.age < 186 && (this.age - 30) % 20 == 0) {
            this.bind(level, caster);
         }

         if (this.age >= 46 && this.age < 206 && (this.age - 46) % 10 == 0) {
            this.drain(level, caster);
         }

         if (this.age == 206) {
            this.snap(level, caster);
         }

         if (this.age == 230) {
            this.takeIn(level, caster);
         }

         if (this.age == 232 && caster instanceof ServerPlayer player) {
            GsAdvancements.bledDry(player, this.kills);
         }

         if (this.age >= 266) {
            this.discard();
         }
      }
   }

   private void bind(ServerLevel level, @Nullable Entity caster) {
      int cap = (Integer)GsConfig.COMMON.maxTargets.get();
      AABB box = new AABB(this.position(), this.position()).inflate(40.0, 24.0, 40.0);
      int added = 0;

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         if (this.boundAt.size() >= cap) {
            break;
         }

         if (!this.boundAt.containsKey(e.getId()) && !(e.position().distanceTo(this.position()) > 40.0)) {
            this.boundAt.put(e.getId(), this.age);
            added++;
         }
      }

      if (added > 0) {
         ThreadsFx.binds(level, this.position().add(0.0, 34.0, 0.0), added);
         this.sync();
      }
   }

   private List<LivingEntity> bound(ServerLevel level) {
      List<LivingEntity> out = new ArrayList<>();
      boolean changed = false;
      Iterator<Entry<Integer, Integer>> it = this.boundAt.entrySet().iterator();

      while (it.hasNext()) {
         Entity e = level.getEntity(it.next().getKey());
         if (e instanceof LivingEntity living && living.isAlive() && e.position().distanceTo(this.position()) < 60.0) {
            out.add(living);
            continue;
         }

         it.remove();
         changed = true;
      }

      if (changed) {
         this.sync();
      }

      return out;
   }

   private void sync() {
      CompoundTag tag = new CompoundTag();
      tag.putIntArray("ids", this.boundAt.keySet().stream().mapToInt(Integer::intValue).toArray());
      tag.putIntArray("at", this.boundAt.values().stream().mapToInt(Integer::intValue).toArray());
      this.entityData.set(DATA_BOUND, tag);
   }

   private void drain(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      List<LivingEntity> targets = this.bound(level);
      float crowd = 1.0F + 0.04F * (float)Math.min(15, targets.size());
      float total = 0.0F;

      for (LivingEntity e : targets) {
         float before = e.getHealth();
         e.invulnerableTime = 0;
         this.hurt(level, caster, e, this.power * 0.15F * crowd * scale);
         float took = Math.max(0.0F, before - e.getHealth());
         total += took;
         this.given.merge(e.getId(), took, Float::sum);
         e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 2, false, true));
         e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30, 1, false, true));
      }

      this.drunk += total;
      if (caster instanceof LivingEntity living && living.isAlive() && total > 0.0F) {
         living.heal(Math.min(total * 0.35F, this.power * 0.5F));
      }
   }

   private void snap(ServerLevel level, @Nullable Entity caster) {
      float scale = ((Double)GsConfig.COMMON.damageMultiplier.get()).floatValue();
      List<LivingEntity> targets = this.bound(level);
      ThreadsFx.snaps(level, this.position().add(0.0, 34.0, 0.0), targets);

      for (LivingEntity e : targets) {
         float extra = this.given.getOrDefault(e.getId(), 0.0F) * 0.3F;
         e.invulnerableTime = 0;
         this.hurt(level, caster, e, this.power * 1.2F * scale + extra);
         Vec3 up = this.position().add(0.0, 34.0, 0.0).subtract(e.position()).normalize();
         e.push(up.x * 0.5, 0.4, up.z * 0.5);
         e.hurtMarked = true;
      }
   }

   private void takeIn(ServerLevel level, @Nullable Entity caster) {
      Vec3 at = caster == null ? this.position() : caster.position();
      ThreadsFx.takenIn(level, at);
      if (caster instanceof LivingEntity living && living.isAlive()) {
         int shield = Mth.clamp((int)(this.drunk / 24.0F), 0, 4);
         living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, shield, false, true));
         living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, this.drunk > 60.0F ? 1 : 0, false, true));
         living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 160, 1, false, true));
      }

      AABB box = new AABB(at, at).inflate(12.0, 6.0, 12.0);

      for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, t -> ArsenalEntity.hostile(t, caster, this.casterId))) {
         Vec3 out = e.position().subtract(at);
         out = out.lengthSqr() < 1.0E-4 ? new Vec3(1.0, 0.0, 0.0) : out.normalize();
         e.push(out.x * 1.4, 0.5, out.z * 1.4);
         e.hurtMarked = true;
      }
   }

   private void hurt(ServerLevel level, @Nullable Entity caster, LivingEntity target, float amount) {
      boolean alive = target.isAlive();
      target.hurt(GsDamage.crimsonThreads(level, (Entity)(caster == null ? this : caster)), amount);
      if (alive && !target.isAlive() && this.dead.add(target.getUUID())) {
         this.kills++;
      }
   }

   @Override
   public void remove(RemovalReason reason) {
      if (this.counted) {
         this.counted = false;
         GsPresence.leave(18);
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
