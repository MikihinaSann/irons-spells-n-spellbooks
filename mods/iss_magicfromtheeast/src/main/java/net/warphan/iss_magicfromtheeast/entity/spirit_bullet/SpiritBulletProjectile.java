package net.warphan.iss_magicfromtheeast.entity.spirit_bullet;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru.SpiritAshigaruEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;

public class SpiritBulletProjectile extends AbstractMagicProjectile {
   public SpiritBulletProjectile(EntityType<? extends SpiritBulletProjectile> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
   }

   public SpiritBulletProjectile(Level level, LivingEntity shooter) {
      this((EntityType<? extends SpiritBulletProjectile>)MFTEEntityRegistries.SPIRIT_BULLET.get(), level);
      this.setOwner(shooter);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      pos = pos.add(this.getDeltaMovement());
      Vec3 random = new Vec3(Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.05F), Utils.getRandomScaled(0.05F));

      for (int i = 0; i < 5; i++) {
         this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, random.x, random.y, random.z);
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 5, 0.2, 0.2, 0.2, 0.2, true);
      MagicManager.spawnParticles(this.level, ParticleTypes.SCULK_SOUL, x, y, z, 3, 0.1, 0.1, 0.1, 0.1, true);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundEvents.SOUL_ESCAPE);
   }

   public float getSpeed() {
      return 3.0F;
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity entity = entityHitResult.getEntity();
      if (entity != this.getOwner() || !this.isAlliedTo(this.getOwner())) {
         if (this.getOwner() instanceof SpiritAshigaruEntity ashigaru) {
            Entity highOwner = ashigaru.getSummoner();
            if (highOwner != null) {
               DamageSources.applyDamage(entity, this.getDamage(), MFTESpellRegistries.ASHIGARU_SQUAD_SPELL.get().getDamageSource(this, highOwner));
            } else {
               DamageSources.applyDamage(entity, this.getDamage(), MFTESpellRegistries.ASHIGARU_SQUAD_SPELL.get().getDamageSource(this, this.getOwner()));
            }
         }

         entity.invulnerableTime = 0;
         this.discard();
      }
   }
}
