package com.somake.somakespells.entity.spells;

import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.sound.ResonantPulseSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ResonantPulseProjectile extends AbstractMagicProjectile {
   public ResonantPulseProjectile(EntityType<? extends AbstractMagicProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public ResonantPulseProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends AbstractMagicProjectile>)ModEntityTypes.RESONANT_PULSE_PROJECTILE.get(), levelIn);
      this.setOwner(shooter);
   }

   public float getSpeed() {
      return 3.0F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return BuiltInRegistries.SOUND_EVENT.getHolder(TunesCompat.HARP_WAVE_SOUND_ID).map(h -> (Holder<SoundEvent>)h);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      DamageSources.applyDamage(entity, this.damage, ((ResonantPulseSpell)ModSpells.RESONANT_PULSE.get()).getDamageSource(this, this.getOwner()));
      Vec3 knockback = this.getDeltaMovement().normalize().scale(1.5);
      entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
      if (entity instanceof LivingEntity livingEntity) {
         livingEntity.hurtMarked = true;
      }

      this.discard();
   }

   public void trailParticles() {
      Vec3 motion = this.getDeltaMovement();
      double speed = motion.length();
      if (!(speed < 0.001)) {
         Vec3 direction = motion.normalize();
         Vec3 reference = Math.abs(direction.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
         Vec3 side = direction.cross(reference);
         if (side.lengthSqr() < 1.0E-4) {
            side = direction.cross(new Vec3(0.0, 0.0, 1.0));
         }

         side = side.normalize();
         Vec3 up = side.cross(direction).normalize();
         ParticleOptions particle = this.getPulseParticle();
         int helixSteps = Math.max(6, Math.min(8, (int)Math.ceil(speed * 2.2)));
         double trailLength = Math.min(1.9, speed * 1.95);
         double phase = this.tickCount * 0.9;

         for (int i = 0; i < helixSteps; i++) {
            double progress = helixSteps == 1 ? 0.0 : (double)i / (helixSteps - 1);
            Vec3 center = this.position().subtract(direction.scale(progress * trailLength));
            double angle = phase + progress * Math.PI * 3.5;
            double radius = 0.32 - progress * 0.1;
            Vec3 helixOffset = side.scale(Math.cos(angle) * radius).add(up.scale(Math.sin(angle) * radius));
            Vec3 particleVelocity = direction.scale(0.01).add(helixOffset.scale(0.02));
            this.level()
               .addParticle(
                  particle,
                  center.x + helixOffset.x,
                  center.y + helixOffset.y,
                  center.z + helixOffset.z,
                  particleVelocity.x,
                  particleVelocity.y,
                  particleVelocity.z
               );
         }

         if ((this.tickCount & 1) == 0) {
            Vec3 tail = this.position().subtract(direction.scale(trailLength * 0.5));
            this.level().addParticle(particle, tail.x, tail.y, tail.z, 0.0, 0.0, 0.0);
         }
      }
   }

   public void impactParticles(double x, double y, double z) {
      ParticleOptions particle = this.getPulseParticle();
      MagicManager.spawnParticles(this.level(), particle, x, y, z, 18, 0.18, 0.18, 0.18, 0.08, true);
      Vec3 motion = this.getDeltaMovement();
      Vec3 direction = motion.lengthSqr() > 1.0E-4 ? motion.normalize() : new Vec3(0.0, 0.0, 1.0);
      Vec3 reference = Math.abs(direction.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
      Vec3 side = direction.cross(reference);
      if (side.lengthSqr() < 1.0E-4) {
         side = direction.cross(new Vec3(0.0, 0.0, 1.0));
      }

      side = side.normalize();
      Vec3 up = side.cross(direction).normalize();
      if (this.level() instanceof ServerLevel serverLevel) {
         for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2) * i / 16.0;
            Vec3 ringOffset = side.scale(Math.cos(angle) * 0.22).add(up.scale(Math.sin(angle) * 0.22));
            Vec3 burstVelocity = direction.scale(0.24).add(ringOffset.scale(0.7));
            serverLevel.sendParticles(
               particle, x + ringOffset.x * 0.35, y + ringOffset.y * 0.35, z + ringOffset.z * 0.35, 0, burstVelocity.x, burstVelocity.y, burstVelocity.z, 0.08
            );
         }

         serverLevel.sendParticles(particle, x + direction.x * 0.2, y + direction.y * 0.2, z + direction.z * 0.2, 8, 0.08, 0.08, 0.08, 0.03);
      }
   }

   private ParticleOptions getPulseParticle() {
      return TunesCompat.getBlackOrMusicalParticle();
   }
}
