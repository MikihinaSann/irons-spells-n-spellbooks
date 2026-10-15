package net.hazen.hazennstuff.Entity.Spells.Lightning.InstantLightningStrike;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningStrike extends AoeEntity {
   private DamageSource damageSource;
   private int waitTime = 0;

   public LightningStrike(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setRadius(3.0F);
      this.setCircular();
   }

   public LightningStrike(Level level) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.LIGHTNING_STRIKE.get(), level);
   }

   public void tick() {
      if (!this.level.isClientSide) {
         if (this.tickCount < this.waitTime) {
            return;
         }

         if (this.tickCount == this.waitTime + 1) {
            this.checkHits();
            MagicManager.spawnParticles(this.level, ParticleHelper.ELECTRIC_SPARKS, this.getX(), this.getY(), this.getZ(), 25, 0.2F, 0.2F, 0.2F, 0.25, true);
            Vec3 bottom = this.position();
            Vec3 top = bottom.add(0.0, 15.0, 0.0);
            Vec3 middle = bottom.add(Utils.getRandomScaled(2.0), Utils.random.nextIntBetweenInclusive(3, 12), Utils.getRandomScaled(2.0));
            MagicManager.spawnParticles(this.level, new ZapParticleOption(top), middle.x, middle.y, middle.z, 1, 0.0, 0.0, 0.0, 0.0, true);
            MagicManager.spawnParticles(this.level, new ZapParticleOption(middle), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true);
            if (Utils.random.nextFloat() < 0.3F) {
               Vec3 split = middle.add(Utils.getRandomScaled(2.0), -Math.abs(Utils.getRandomScaled(2.0)), Utils.getRandomScaled(2.0));
               MagicManager.spawnParticles(this.level, new ZapParticleOption(middle), split.x, split.y, split.z, 1, 0.0, 0.0, 0.0, 0.0, true);
            }

            this.playSound((SoundEvent)SoundRegistry.SMALL_LIGHTNING_STRIKE.get(), 2.0F, 0.8F + this.random.nextFloat() * 0.5F);
         }

         if (this.tickCount > this.waitTime + 5) {
            this.discard();
         }
      }
   }

   public void applyEffect(LivingEntity target) {
      if (target != this.getOwner()) {
         if (this.damageSource == null) {
            this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.LIGHTNING_MAGIC), this, this.getOwner());
         }

         DamageSources.ignoreNextKnockback(target);
         target.hurt(this.damageSource, this.getDamage());
      }
   }

   public void setWaitTime(int waitTime) {
      this.waitTime = waitTime;
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }
}
