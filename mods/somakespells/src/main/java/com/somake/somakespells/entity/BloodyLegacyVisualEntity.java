package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BloodyLegacyVisualEntity extends Entity implements GeoEntity {
   private static final int SEGMENT_COUNT = 16;
   private static final int LIFETIME_TICKS = 24;
   private static final float MODEL_LENGTH_BLOCKS = 3.9375F;
   private static final float HIT_WIDTH = 1.35F;
   private static final ResourceLocation BLOOD_IMPACT_SOUND_ID = ResourceLocation.parse("irons_spellbooks:spell.sacrifice.blood_explosion");
   private static final EntityDataAccessor<Float> RANGE = SynchedEntityData.defineId(BloodyLegacyVisualEntity.class, EntityDataSerializers.FLOAT);
   private static final RawAnimation CAST_ANIMATION = RawAnimation.begin().thenPlayAndHold("cast");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private Vec3 lineStart = Vec3.ZERO;
   private float damage;
   private LivingEntity caster;
   private SpellDamageSource damageSource;

   public BloodyLegacyVisualEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public BloodyLegacyVisualEntity(Level level, double x, double y, double z, float yRot, float range) {
      this((EntityType<?>)ModEntityTypes.BLOODY_LEGACY_VISUAL.get(), level);
      this.setPos(x, y, z);
      this.setYRot(yRot);
      this.setRange(range);
   }

   public BloodyLegacyVisualEntity(Level level, double x, double y, double z, float yRot, float range, Vec3 lineStart, float damage, LivingEntity caster) {
      this(level, x, y, z, yRot, range, lineStart, damage, caster, null);
   }

   public BloodyLegacyVisualEntity(
      Level level, double x, double y, double z, float yRot, float range, Vec3 lineStart, float damage, LivingEntity caster, SpellDamageSource damageSource
   ) {
      this(level, x, y, z, yRot, range);
      this.lineStart = lineStart;
      this.damage = damage;
      this.caster = caster;
      this.damageSource = damageSource;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(RANGE, 3.9375F);
   }

   public void setRange(float range) {
      this.entityData.set(RANGE, range);
   }

   public float getRange() {
      return (Float)this.entityData.get(RANGE);
   }

   public float getLengthScale() {
      return this.getRange() / 3.9375F;
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel serverLevel && this.tickCount >= 1 && this.tickCount <= 16) {
         this.triggerSegment(serverLevel, this.tickCount - 1);
      }

      if (!this.level().isClientSide && this.tickCount > 24) {
         this.discard();
      }
   }

   private void triggerSegment(ServerLevel serverLevel, int segmentIndex) {
      float range = this.getRange();
      double progress = (segmentIndex + 0.5) / 16.0;
      double forwardDistance = -range * 0.5 + range * progress;
      Vec3 forward = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
      Vec3 segmentCenter = this.position().add(forward.scale(forwardDistance));
      double groundY = this.findGroundY(serverLevel, segmentCenter.x, this.getY() + 4.0, segmentCenter.z);
      double x = segmentCenter.x;
      double y = groundY + 0.08;
      double z = segmentCenter.z;
      serverLevel.sendParticles(new BlastwaveParticleOptions(0.82F, 0.12F, 0.16F, 0.55F), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
      MagicManager.spawnParticles(serverLevel, ParticleHelper.BLOOD, x, y + 0.04, z, 10, 0.14, 0.04, 0.14, 0.015, true);
      MagicManager.spawnParticles(serverLevel, ParticleHelper.BLOOD, x, y + 0.12, z, 8, 0.08, 0.12, 0.08, 0.02, true);
      this.damageSegment(serverLevel, forward, x, y, z, segmentIndex);
      SoundEvent bloodImpact = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(BLOOD_IMPACT_SOUND_ID);
      serverLevel.playSound(null, x, y, z, bloodImpact, SoundSource.PLAYERS, 0.8F, 1.0F + segmentIndex * 0.015F);
   }

   private void damageSegment(ServerLevel serverLevel, Vec3 forward, double x, double y, double z, int segmentIndex) {
      if (this.caster != null && this.caster.isAlive() && !(this.damage <= 0.0F) && this.damageSource != null) {
         AABB area = new AABB(x - 1.650000023841858, y - 0.4, z - 1.650000023841858, x + 1.650000023841858, y + 3.4, z + 1.650000023841858);

         for (LivingEntity target : serverLevel.getEntitiesOfClass(
            LivingEntity.class, area, entity -> entity.isAlive() && entity != this.caster && !entity.isAlliedTo(this.caster)
         )) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            double dx = targetCenter.x - x;
            double dz = targetCenter.z - z;
            if (!(dx * dx + dz * dz > 1.8225001F)) {
               double along = targetCenter.subtract(this.lineStart).dot(forward);
               int expectedSegment = Mth.clamp((int)Math.floor(along / this.getRange() * 16.0), 0, 15);
               if (Math.abs(expectedSegment - segmentIndex) <= 1) {
                  boolean damaged = DamageSources.applyDamage(target, this.damage, this.damageSource);
                  if (damaged) {
                     MagicManager.spawnParticles(
                        serverLevel, ParticleHelper.BLOOD, targetCenter.x, targetCenter.y, targetCenter.z, 12, 0.2, 0.35, 0.2, 0.03, true
                     );
                  }
               }
            }
         }
      }
   }

   private double findGroundY(Level level, double x, double startY, double z) {
      Vec3 start = new Vec3(x, startY, z);
      Vec3 end = new Vec3(x, startY - 8.0, z);
      BlockHitResult result = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, CollisionContext.empty()));
      return result.getType() == Type.BLOCK ? result.getLocation().y : startY - 3.5;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.setRange(compound.getFloat("Range"));
      if (compound.contains("LineStartX")) {
         this.lineStart = new Vec3(compound.getDouble("LineStartX"), compound.getDouble("LineStartY"), compound.getDouble("LineStartZ"));
      }

      this.damage = compound.getFloat("Damage");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("Range", this.getRange());
      compound.putDouble("LineStartX", this.lineStart.x);
      compound.putDouble("LineStartY", this.lineStart.y);
      compound.putDouble("LineStartZ", this.lineStart.z);
      compound.putFloat("Damage", this.damage);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, event -> event.setAndContinue(CAST_ANIMATION)));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
