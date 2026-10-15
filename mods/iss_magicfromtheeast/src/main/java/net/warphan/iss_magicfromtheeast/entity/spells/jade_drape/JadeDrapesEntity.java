package net.warphan.iss_magicfromtheeast.entity.spells.jade_drape;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.OwnerHelper;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JadeDrapesEntity extends AbstractShieldEntity implements GeoEntity, IMagicSummon {
   private static final EntityDataAccessor<Boolean> DATA_IS_OPENING = SynchedEntityData.defineId(JadeDrapesEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_CLOSING = SynchedEntityData.defineId(JadeDrapesEntity.class, EntityDataSerializers.BOOLEAN);
   protected ShieldPart[] subEntities;
   protected final Vec3[] subPositions;
   protected final int LIFETIME;
   protected int width;
   protected int height;
   protected int age;
   protected UUID summonerUUID;
   protected LivingEntity cachedSummoner;
   private int openTick = 15;
   private int closeTick = 10;
   public float percentReflectDamage;
   private final RawAnimation OPEN = RawAnimation.begin().thenPlay("open");
   private final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
   private final RawAnimation CLOSE = RawAnimation.begin().thenPlay("close");
   private final AnimationController<JadeDrapesEntity> openController = new AnimationController(this, "open_control", 0, this::openPredicate);
   private final AnimationController<JadeDrapesEntity> idleController = new AnimationController(this, "idle_control", 0, this::idlePredicate);
   private final AnimationController<JadeDrapesEntity> closeController = new AnimationController(this, "close_control", 0, this::closePredicate);
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public JadeDrapesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.width = 11;
      this.height = 7;
      this.subEntities = new ShieldPart[this.width * this.height];
      this.subPositions = new Vec3[this.width * this.height];
      this.setHealth(10.0F);
      this.LIFETIME = 400;
      this.createShield();
   }

   public JadeDrapesEntity(Level level, float health, LivingEntity caster, boolean playOpenAnimation) {
      this((EntityType<?>)MFTEEntityRegistries.JADE_DRAPES_ENTITY.get(), level);
      this.setHealth(health);
      this.setSummoner(caster);
      if (playOpenAnimation) {
         this.triggerOpenAnimation();
      }
   }

   public void setPercentReflectDamage(float percent) {
      this.percentReflectDamage = percent;
   }

   public LivingEntity getSummoner() {
      return OwnerHelper.getAndCacheOwner(this.level(), this.cachedSummoner, this.summonerUUID);
   }

   public void setSummoner(@Nullable LivingEntity summoner) {
      if (summoner != null) {
         this.summonerUUID = summoner.getUUID();
         this.cachedSummoner = summoner;
      }
   }

   public void onUnSummon() {
      this.isAnimatingClose();
      this.entityData.set(DATA_IS_CLOSING, true);
   }

   protected void createShield() {
      for (int x = 0; x < this.width; x++) {
         for (int y = 0; y < this.height; y++) {
            int i = x * this.height + y;
            this.subEntities[i] = new ShieldPart(this, "part" + (i + 1), 0.5F, 0.5F, true);
            this.subPositions[i] = new Vec3((x - this.width / 2.0F) * 0.5F + 0.25F, (y - this.height / 2.0F) * 0.5F, 0.0);
         }
      }
   }

   public void setRotation(float y) {
      this.setYRot(y);
      this.yRotO = y;
   }

   public void takeDamage(DamageSource source, float amount, @Nullable Vec3 location) {
      if (!this.isInvulnerableTo(source)) {
         this.setHealth(this.getHealth() - amount);
         if (!this.level().isClientSide && location != null) {
            MagicManager.spawnParticles(this.level(), ParticleTypes.SCRAPE, location.x, location.y, location.z, 8, 0.1, 0.1, 0.1, 0.5, false);
            MagicManager.spawnParticles(this.level(), MFTEParticleHelper.JADE_SHATTER, location.x, location.y, location.z, 3, 0.1, 0.1, 0.1, 0.3, false);
            this.level().playSound(null, location.x, location.y, location.z, (SoundEvent)SoundRegistry.FORCE_IMPACT.get(), SoundSource.NEUTRAL, 0.8F, 1.0F);
         }
      }
   }

   public void tick() {
      this.hurtThisTick = false;
      if (this.isAnimatingOpen() && --this.openTick < 0) {
         this.entityData.set(DATA_IS_OPENING, false);
         this.setXRot(0.0F);
         this.setOldPosAndRot();
      }

      if (this.getHealth() <= 0.0F) {
         this.destroy();
      } else if (++this.age > this.LIFETIME) {
         this.onUnSummon();
      } else {
         for (int i = 0; i < this.subEntities.length; i++) {
            ShieldPart subEntity = this.subEntities[i];
            Vec3 pos = this.subPositions[i]
               .xRot((float) (Math.PI / 180.0) * -this.getXRot())
               .yRot((float) (Math.PI / 180.0) * -this.getYRot())
               .add(this.position());
            subEntity.setPos(pos);
            subEntity.xo = pos.x;
            subEntity.yo = pos.y;
            subEntity.zo = pos.z;
            subEntity.xOld = pos.x;
            subEntity.yOld = pos.y;
            subEntity.zOld = pos.z;
         }
      }

      if (this.isAnimatingClose() && --this.closeTick < 0) {
         this.entityData.set(DATA_IS_CLOSING, false);
         this.discard();
      }
   }

   public PartEntity<?>[] getParts() {
      return this.subEntities;
   }

   protected void destroy() {
      if (!this.level().isClientSide) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.NEUTRAL, 2.0F, 0.65F);
      }

      super.destroy();
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_OPENING, false);
      pBuilder.define(DATA_IS_CLOSING, false);
   }

   public boolean isAnimatingOpen() {
      return (Boolean)this.entityData.get(DATA_IS_OPENING);
   }

   public boolean isAnimatingClose() {
      return (Boolean)this.entityData.get(DATA_IS_CLOSING);
   }

   public void triggerOpenAnimation() {
      this.entityData.set(DATA_IS_OPENING, true);
   }

   public double getTick(Object o) {
      return this.tickCount;
   }

   private PlayState openPredicate(AnimationState event) {
      if (!this.isAnimatingOpen()) {
         return PlayState.STOP;
      } else {
         if (event.getController().getAnimationState() == State.STOPPED) {
            event.getController().setAnimation(this.OPEN);
         }

         return PlayState.CONTINUE;
      }
   }

   private PlayState idlePredicate(AnimationState event) {
      if (!this.isAnimatingClose() && !this.isAnimatingClose()) {
         event.getController().setAnimation(this.IDLE);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState closePredicate(AnimationState event) {
      if (this.isAnimatingClose()) {
         event.getController().setAnimation(this.CLOSE);
      }

      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(this.openController);
      controllerRegistrar.add(this.idleController);
      controllerRegistrar.add(this.closeController);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
