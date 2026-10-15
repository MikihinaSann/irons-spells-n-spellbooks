package net.warphan.iss_magicfromtheeast.entity.spells.jade_bullet;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEParticleRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class JadeBulletProjectile extends AbstractMagicProjectile {
   public JadeBulletProjectile(EntityType<? extends JadeBulletProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public JadeBulletProjectile(Level level, LivingEntity caster) {
      this((EntityType<? extends JadeBulletProjectile>)MFTEEntityRegistries.JADE_PROJECTILE.get(), level);
      this.setOwner(caster);
   }

   public void trailParticles() {
      float yHeading = -((float)(Mth.atan2(this.getDeltaMovement().z, this.getDeltaMovement().x) * 180.0F / (float)Math.PI) + 90.0F);
      float radius = 0.25F;
      int steps = 2;
      Vec3 vec = this.getDeltaMovement();
      double x2 = this.getX();
      double x1 = x2 - vec.x;
      double y2 = this.getY();
      double y1 = y2 - vec.y;
      double z2 = this.getZ();
      double z1 = z2 - vec.z;

      for (int j = 0; j < steps; j++) {
         float offset = 1.0F / steps * j;
         double radians = (this.tickCount + offset) / 7.5F * 360.0F * (float) (Math.PI / 180.0);
         Vec3 swirl = new Vec3(Math.cos(radians) * radius, Math.sin(radians) * radius, 0.0).yRot(yHeading * (float) (Math.PI / 180.0));
         double x = Mth.lerp(offset, x1, x2) + swirl.x;
         double y = Mth.lerp(offset, y1, y2) + swirl.y + this.getBbHeight() / 2.0F;
         double z = Mth.lerp(offset, z1, z2) + swirl.z;
         Vec3 jitter = Vec3.ZERO;
         this.level.addParticle(ParticleTypes.GLOW, x, y, z, jitter.x, jitter.y, jitter.z);
      }

      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      pos = pos.add(this.getDeltaMovement());
      Vec3 random = new Vec3(Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.05F));

      for (int i = 0; i < 3; i++) {
         this.level
            .addParticle(
               new DustParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), 1.8F), pos.x, pos.y, pos.z, random.x, random.y, random.z
            );
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, (ParticleOptions)MFTEParticleRegistries.JADE_SHATTER_PARTICLE.get(), x, y, z, 8, 0.3, 0.3, 0.3, 0.1, true);
   }

   public float getSpeed() {
      return 1.6F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(MFTESoundRegistries.JADE_SWORD_IMPACT);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.doAoeDamage(blockHitResult.getLocation());
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      DamageSources.applyDamage(entity, this.getDamage(), MFTESpellRegistries.JADE_BULLET_SPELL.get().getDamageSource(this, this.getOwner()));
      this.doAoeDamage(entityHitResult.getLocation());
      this.discard();
   }

   private void doAoeDamage(Vec3 location) {
      float boomRadius = 2.5F;
      this.level.getEntities(this, this.getBoundingBox().inflate(boomRadius)).forEach(entity -> {
         if (this.canHitEntity(entity)) {
            double distance = entity.distanceToSqr(this.position());
            if (distance < boomRadius * boomRadius) {
               float damage = this.damage;
               DamageSources.applyDamage(entity, damage * 0.5F, MFTESpellRegistries.JADE_BULLET_SPELL.get().getDamageSource(this, this.getOwner()));
            }
         }
      });
      if (!this.level.isClientSide) {
         MagicManager.spawnParticles(
            this.level,
            new BlastwaveParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), 1.2F),
            location.x,
            location.y + 0.165F,
            location.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
      }
   }
}
