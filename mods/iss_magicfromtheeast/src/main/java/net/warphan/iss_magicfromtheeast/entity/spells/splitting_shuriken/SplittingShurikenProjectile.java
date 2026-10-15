package net.warphan.iss_magicfromtheeast.entity.spells.splitting_shuriken;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SplittingShurikenProjectile extends AbstractMagicProjectile implements GeoEntity {
   private static final EntityDataAccessor<Boolean> IS_PRIMARY = SynchedEntityData.defineId(SplittingShurikenProjectile.class, EntityDataSerializers.BOOLEAN);
   public int splitCount;
   private final RawAnimation shuriken_spin = RawAnimation.begin().thenPlay("spin");
   public AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SplittingShurikenProjectile(EntityType<? extends SplittingShurikenProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public SplittingShurikenProjectile(Level level, LivingEntity caster) {
      this((EntityType<? extends SplittingShurikenProjectile>)MFTEEntityRegistries.SPIRIT_SHURIKEN.get(), level);
      this.setOwner(caster);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(IS_PRIMARY, false);
   }

   public void setIsPrimary() {
      this.entityData.set(IS_PRIMARY, true);
   }

   public boolean isPrimary() {
      return (Boolean)this.entityData.get(IS_PRIMARY);
   }

   public void setSplitCount(int count) {
      this.splitCount = count;
   }

   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleTypes.SCULK_SOUL, x, y, z, 3, 0.2, 0.2, 0.2, 0.1, true);
   }

   public float getSpeed() {
      return 1.5F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundEvents.SOUL_ESCAPE);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      Vec3 center = entityHitResult.getLocation();
      Level level = this.level;
      DamageSources.applyDamage(entity, this.getDamage(), MFTESpellRegistries.SPLITTING_SHURIKEN.get().getDamageSource(this, this.getOwner()));
      if (this.isPrimary() && this.getOwner() instanceof LivingEntity livingEntity && this.splitCount > 0) {
         int count = this.splitCount;
         int offset = 360 / count;

         for (int i = 0; i < count; i++) {
            Vec3 motion = new Vec3(0.0, 0.0, 1.0);
            motion = motion.xRot((float) (Math.PI / 180.0));
            motion = motion.yRot(offset * i * (float) (Math.PI / 180.0));
            SplittingShurikenProjectile splitShuriken = new SplittingShurikenProjectile(level, livingEntity);
            splitShuriken.setDamage(this.getDamage() / 2.0F);
            splitShuriken.setDeltaMovement(motion);
            splitShuriken.moveTo(center.x, center.y, center.z);
            level.addFreshEntity(splitShuriken);
         }
      }

      this.discard();
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.shuriken_spin);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
