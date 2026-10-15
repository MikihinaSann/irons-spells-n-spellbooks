package net.acetheeldritchking.cataclysm_spellbooks.entity.spells.infernal_blade;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class InfernalBladeProjectile extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Boolean> SOUL = SynchedEntityData.defineId(InfernalBladeProjectile.class, EntityDataSerializers.BOOLEAN);
   private final AnimationController<InfernalBladeProjectile> animationController = new AnimationController(this, "controller", 0, this::predicate);

   public InfernalBladeProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public InfernalBladeProjectile(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)CSEntityRegistry.INFERNAL_BLADE_PROJECTILE.get(), level);
      this.setOwner(shooter);
   }

   public void travel() {
      this.setPos(this.position().add(this.getDeltaMovement()));
      if (!this.isNoGravity()) {
         Vec3 vec3 = this.getDeltaMovement();
         this.setDeltaMovement(vec3.x, vec3.y - 0.05F, vec3.z);
      }
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   public void tick() {
      Vec3 deltaMovement = this.getDeltaMovement();
      double distance = deltaMovement.horizontalDistance();
      double x = deltaMovement.x;
      double y = deltaMovement.y;
      double z = deltaMovement.z;
      this.setYRot((float)(Mth.atan2(x, z) * (180.0 / Math.PI)));
      this.setXRot((float)(Mth.atan2(y, distance) * (180.0 / Math.PI)));
      this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
      this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
      super.tick();
   }

   public void trailParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement());
      this.level().addParticle(ParticleHelper.EMBERS, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), (ParticleOptions)ModParticle.TRAP_FLAME.get(), x, y, z, 5, 0.0, 0.0, 0.0, 1.0, true);
   }

   public float getSpeed() {
      return 0.7F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(ModSounds.IGNIS_POKE);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity target = pResult.getEntity();
      DamageSources.applyDamage(target, this.damage, SpellRegistries.INFERNAL_STRIKE.get().getDamageSource(this, this.getOwner()));
      if (target instanceof LivingEntity livingTarget) {
         livingTarget.addEffect(new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND, 100, 0));
      }

      this.discard();
   }

   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
      this.discard();
   }

   public boolean getIsSoul() {
      return (Boolean)this.entityData.get(SOUL);
   }

   public void setIsSoul(boolean soul) {
      this.entityData.set(SOUL, soul);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.animationController);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }

   private PlayState predicate(AnimationState<InfernalBladeProjectile> event) {
      event.getController().setAnimation(RawAnimation.begin().then("animation.infernal_blade_small.idle", LoopType.LOOP));
      return PlayState.CONTINUE;
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(SOUL, false);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setIsSoul(pCompound.getBoolean("Soul"));
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("Soul", this.getIsSoul());
   }
}
