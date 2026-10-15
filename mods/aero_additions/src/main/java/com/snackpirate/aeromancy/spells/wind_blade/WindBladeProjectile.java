package com.snackpirate.aeromancy.spells.wind_blade;

import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

public class WindBladeProjectile extends AbstractMagicProjectile implements IEntityWithComplexSpawn {
   public void trailParticles() {
      for (int i = 0; i < 1; i++) {
         double speed = 0.05;
         double dx = Utils.random.nextDouble() * 2.0 * speed - speed;
         double dy = Utils.random.nextDouble() * 2.0 * speed - speed;
         double dz = Utils.random.nextDouble() * 2.0 * speed - speed;
         this.level()
            .addParticle(
               Utils.random.nextDouble() < 0.3 ? ParticleTypes.SPIT : ParticleTypes.POOF, this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz
            );
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), ParticleTypes.SPIT, x, y, z, 5, 0.1, 0.1, 0.1, 0.25, true);
   }

   public float getSpeed() {
      return 1.75F;
   }

   public WindBladeProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)AASpells.Entities.WIND_BLADE_PROJECTILE.get(), levelIn);
      this.setOwner(shooter);
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundEvents.WIND_CHARGE_BURST);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity target = entityHitResult.getEntity();
      if (target instanceof LivingEntity livingTarget) {
         livingTarget.knockback(this.getDamage(), this.getX() - target.getX(), this.getZ() - target.getZ());
      }

      DamageSources.applyDamage(target, this.getDamage(), AASpells.WIND_BLADE.get().getDamageSource(this, this.getOwner()));
      this.discard();
   }

   public WindBladeProjectile(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }
}
