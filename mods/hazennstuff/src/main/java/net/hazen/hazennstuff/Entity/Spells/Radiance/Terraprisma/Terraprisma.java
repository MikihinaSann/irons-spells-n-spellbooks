package net.hazen.hazennstuff.Entity.Spells.Radiance.Terraprisma;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class Terraprisma extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int delay = 0;
   private int age = 0;
   private Vec3 spawnPos;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.terraprisma.idle_alt");
   private float damage = 2.0F;

   public Terraprisma(EntityType<? extends Terraprisma> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public void tick() {
      this.age++;
      if (this.age < this.delay) {
         if (this.spawnPos != null) {
            this.setPos(this.spawnPos);
         }
      } else {
         super.tick();
      }
   }

   public void setSpawnPos(Vec3 pos) {
      this.spawnPos = pos;
      if (pos != null) {
         this.setPos(pos);
      }
   }

   public void setDelay(int delay) {
      this.delay = delay;
   }

   public Terraprisma(EntityType<? extends Terraprisma> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.setOwner(shooter);
   }

   public Terraprisma(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends Terraprisma>)HnSEntityRegistry.TERRAPRISMA_PROJECTILE.get(), levelIn, shooter);
   }

   public void trailParticles() {
      Vec3 vec = this.getDeltaMovement();
      double length = vec.length();
      int count = (int)Math.min(20L, Math.round(length) * 3L) + 1;
      float f = (float)length / count;

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(0.02);
         Vec3 p = vec.scale(f * i);
         this.level
            .addParticle(
               ParticleHelper.UNSTABLE_ENDER,
               this.getX() + random.x + p.x,
               this.getY() + random.y + p.y,
               this.getZ() + random.z + p.z,
               random.x,
               random.y,
               random.z
            );
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleHelper.UNSTABLE_ENDER, x, y, z, 25, 0.0, 0.0, 0.0, 0.18, true);
   }

   public float getSpeed() {
      return 2.5F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.TERRARIA_IMPACT);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      DamageSources.applyDamage(
         entityHitResult.getEntity(), this.damage, ((AbstractSpell)SpellRegistry.MAGIC_MISSILE_SPELL.get()).getDamageSource(this, this.getOwner())
      );
      this.discard();
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.idle);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }
}
