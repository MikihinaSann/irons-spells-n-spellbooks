package net.hazen.hazennstuff.Entity.Spells.Holy.IchorStream;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEffects;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class IchorStream extends AbstractMagicProjectile {
   private int spellLevel;
   private float spellPower;

   public IchorStream(EntityType<? extends Projectile> entityType, Level world) {
      super(entityType, world);
      this.setNoGravity(false);
   }

   public IchorStream(Level world, LivingEntity shooter, int spellLevel, float spellPower) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.ICHOR_STREAM.get(), world);
      this.setOwner(shooter);
      this.spellLevel = spellLevel;
      this.spellPower = spellPower;
      this.setNoGravity(false);
      Vec3 direction = shooter.getLookAngle();
      this.setDeltaMovement(direction.add(0.0, 0.6, 0.0).normalize().scale(0.8));
      this.setPos(shooter.getEyePosition());
   }

   public void trailParticles() {
      this.level.addParticle(HnSParticleHelper.ICHOR_TRAIL, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, HnSParticleHelper.ICHOR_TRAIL, x, y, z, 10, 0.1, 0.1, 0.1, 0.2, false);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         this.level.addParticle(HnSParticleHelper.ICHOR_TRAIL, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }

      if (this.tickCount > 20) {
         this.discard();
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      if (!this.level.isClientSide && entityHitResult.getEntity() instanceof LivingEntity target) {
         int baseDuration = 100 + this.spellLevel * 40;
         int scaledDuration = baseDuration + (int)(this.spellPower * 20.0F);
         int baseAmplifier = Math.max(0, (this.spellLevel - 1) / 2);
         int scaledAmplifier = baseAmplifier + (int)(this.spellPower / 10.0F);
         target.addEffect(new MobEffectInstance(HnSEffects.ICHOR, scaledDuration, scaledAmplifier, false, true, true));
         float damage = this.spellPower * 0.3F;
         DamageSources.applyDamage(target, damage, HnSSpellRegistries.GOLDEN_SHOWER.get().getDamageSource(this, this.getOwner()));
      }

      this.discard();
   }

   public float getSpeed() {
      return 1.4F;
   }
}
