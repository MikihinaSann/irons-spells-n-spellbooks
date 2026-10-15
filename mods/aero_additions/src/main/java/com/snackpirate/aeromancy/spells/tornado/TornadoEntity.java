package com.snackpirate.aeromancy.spells.tornado;

import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags.EntityTypes;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;

public class TornadoEntity extends Projectile implements AntiMagicSusceptible, GeoAnimatable {
   private static final EntityDataAccessor<Float> DATA_SIZE = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_EFFECT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
   private static final int loopSoundDurationInTicks = 320;
   private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
   private final AnimationController<TornadoEntity> controller = new AnimationController(this, "controller", 0, this::predicate);
   List<Entity> trackingEntities = new ArrayList<>();

   private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
      animationState.getController().setAnimation(RawAnimation.begin().then("spin", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   public TornadoEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public TornadoEntity(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends Projectile>)AASpells.Entities.TORNADO.get(), pLevel);
      this.setOwner(owner);
   }

   public void setSize(float size) {
      if (!this.level().isClientSide) {
         this.getEntityData().set(DATA_SIZE, Math.min(size, 48.0F));
      }
   }

   public float getSize() {
      return (Float)this.getEntityData().get(DATA_SIZE);
   }

   public void setEffect(TornadoEntity.TornadoEffect effect) {
      if (!this.level().isClientSide) {
         this.getEntityData().set(DATA_EFFECT, effect.id());
      }
   }

   public TornadoEntity.TornadoEffect getEffect() {
      return TornadoEntity.TornadoEffect.BY_ID.apply((Integer)this.getEntityData().get(DATA_EFFECT));
   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (DATA_SIZE.equals(pKey)) {
         this.refreshDimensions();
         if (this.getSize() < 0.1F) {
            this.discard();
         }
      }

      super.onSyncedDataUpdated(pKey);
   }

   protected void addAdditionalSaveData(CompoundTag pCompound) {
      pCompound.putFloat("Size", this.getSize());
      pCompound.putInt("Age", this.tickCount);
      pCompound.putInt("Effect", this.getEffect().id());
      super.addAdditionalSaveData(pCompound);
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      this.tickCount = pCompound.getInt("Age");
      this.setEffect(TornadoEntity.TornadoEffect.BY_ID.apply(pCompound.getInt("Effect")));
      if (pCompound.getInt("Size") > 0) {
         this.setSize(pCompound.getFloat("Size"));
      }

      super.readAdditionalSaveData(pCompound);
   }

   public void onAntiMagic(MagicData playerMagicData) {
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_SIZE, 1.0F);
      builder.define(DATA_EFFECT, TornadoEntity.TornadoEffect.DEFAULT.id());
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.controller);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public double getTick(Object object) {
      return this.tickCount;
   }

   public void onAddedToLevel() {
      super.onAddedToLevel();
   }

   public boolean displayFireAnimation() {
      return false;
   }

   @NotNull
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 1.5F, this.getSize());
   }

   public void tick() {
      super.tick();
      int update = Math.max((int)(this.getSize() / 2.0F), 2);
      if (this.tickCount % update == 0) {
         this.updateTrackingEntities();
      }

      AABB bb = this.getBoundingBox();
      float radius = (float)bb.getXsize();
      boolean hitTick = this.tickCount % 10 == 0;

      for (Entity entity : this.trackingEntities) {
         if (entity != this.getOwner() && !DamageSources.isFriendlyFireBetween(this.getOwner(), entity) && !entity.isSpectator()) {
            Vec3 centerFromY = bb.getBottomCenter();
            centerFromY = new Vec3(centerFromY.x, entity.getY(), centerFromY.z);
            float distanceToColumn = (float)centerFromY.distanceTo(entity.position());
            if (!(distanceToColumn > radius)) {
               if (distanceToColumn < 2.0F) {
                  this.checkForApplicableEffects(entity);
               }

               if (hitTick && entity instanceof LivingEntity living && distanceToColumn < 2.0F) {
                  this.applyDamage(living);
               }

               float f = 1.0F - distanceToColumn / radius;
               float heightInColumn = Math.max((float)(entity.getY() - bb.getMinPosition().y), 1.0F) / 3.0F;
               float scale = f * f * f * f * 0.25F;
               Vec3 inwardsSuction = centerFromY.subtract(entity.position()).normalize().scale(scale * heightInColumn);
               Vec3 spinny = inwardsSuction.yRot((float) (Math.PI * 3.0 / 2.0)).scale(scale * 2.0F * heightInColumn);
               if (entity.position().y > bb.getMaxPosition().y - 1.5) {
                  spinny = spinny.scale(4.0);
                  if (entity instanceof LivingEntity living && !living.hasEffect(MobEffectRegistry.AIRBORNE)) {
                     living.addEffect(new MobEffectInstance(MobEffectRegistry.AIRBORNE, 200, (int)this.getSize()));
                  }
               }

               float resistance = entity instanceof LivingEntity livingEntity
                  ? Mth.clamp(1.0F - (float)livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), 0.3F, 1.0F)
                  : 1.0F;
               float bossResistance = entity.getType().is(EntityTypes.BOSSES) ? 0.5F : 1.0F;
               Vec3 pushForce = new Vec3(0.0, scale * resistance * bossResistance * 0.7, 0.0).add(inwardsSuction).add(spinny);
               entity.push(pushForce);
               entity.fallDistance = 0.0F;
            }
         }
      }

      if (!this.level().isClientSide) {
         if (this.tickCount > 6400) {
            this.discard();
         } else if ((this.tickCount - 1) % 320 == 0) {
         }
      }
   }

   public void applyDamage(LivingEntity e) {
      switch (this.getEffect()) {
         case DEFAULT:
         default:
            break;
         case FIRE:
            DamageSources.applyDamage(e, 2.0F, ((AbstractSpell)SpellRegistry.FIRE_BREATH_SPELL.get()).getDamageSource(this, this.getOwner()));
            break;
         case ENDER:
            DamageSources.applyDamage(e, 2.0F, ((AbstractSpell)SpellRegistry.DRAGON_BREATH_SPELL.get()).getDamageSource(this, this.getOwner()));
            break;
         case NATURE:
            if (DamageSources.applyDamage(e, 2.0F, ((AbstractSpell)SpellRegistry.POISON_BREATH_SPELL.get()).getDamageSource(this, this.getOwner()))
               && e instanceof LivingEntity) {
               e.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            }
            break;
         case LIGHTNING:
            DamageSources.applyDamage(e, 2.0F, ((AbstractSpell)SpellRegistry.ELECTROCUTE_SPELL.get()).getDamageSource(this, this.getOwner()));
            if (!this.level().isClientSide()) {
               MagicManager.spawnParticles(
                  this.level(),
                  ParticleHelper.ELECTRICITY,
                  e.getX(),
                  e.getY() + e.getBbHeight() / 2.0F,
                  e.getZ(),
                  10,
                  e.getBbWidth() / 3.0F,
                  e.getBbHeight() / 3.0F,
                  e.getBbWidth() / 3.0F,
                  0.1,
                  false
               );
            }
      }
   }

   public void checkForApplicableEffects(Entity e) {
      for (TornadoEntity.TornadoEffect effect : TornadoEntity.TornadoEffect.values()) {
         if (e.getType().equals(effect.entity)) {
            this.setEffect(effect);
         }
      }
   }

   private void updateTrackingEntities() {
      this.trackingEntities = this.level().getEntities(this, this.getBoundingBox().inflate(1.0));
   }

   public static enum TornadoEffect {
      DEFAULT(0, (EntityType<?>)AASpells.Entities.MAGIC_WIND_CHARGE.get(), AASpells.WIND_CHARGE.get()),
      FIRE(1, (EntityType<?>)EntityRegistry.FIRE_BREATH_PROJECTILE.get(), (AbstractSpell)SpellRegistry.FIRE_BREATH_SPELL.get()),
      ENDER(2, (EntityType<?>)EntityRegistry.DRAGON_BREATH_PROJECTILE.get(), (AbstractSpell)SpellRegistry.DRAGON_BREATH_SPELL.get()),
      NATURE(3, (EntityType<?>)EntityRegistry.POISON_BREATH_PROJECTILE.get(), (AbstractSpell)SpellRegistry.POISON_BREATH_SPELL.get()),
      LIGHTNING(4, (EntityType<?>)EntityRegistry.ELECTROCUTE_PROJECTILE.get(), (AbstractSpell)SpellRegistry.ELECTROCUTE_SPELL.get());

      public static final IntFunction<TornadoEntity.TornadoEffect> BY_ID = ByIdMap.continuous(
         TornadoEntity.TornadoEffect::id, values(), OutOfBoundsStrategy.ZERO
      );
      private final int id;
      private final EntityType<?> entity;
      private final AbstractSpell spell;

      private TornadoEffect(int id, EntityType<?> entity, AbstractSpell spell) {
         this.id = id;
         this.entity = entity;
         this.spell = spell;
      }

      public int id() {
         return this.id;
      }

      public EntityType<?> entity() {
         return this.entity;
      }

      public AbstractSpell spell() {
         return this.spell;
      }
   }
}
