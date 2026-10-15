package net.hazen.hazennstuff.Entity.Spells.Ice.IceCloud;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class IceCloud extends AoeEntity {
   private DamageSource damageSource;

   public IceCloud(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public IceCloud(Level level) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.ICE_CLOUD.get(), level);
   }

   public void applyEffect(LivingEntity target) {
      if (this.damageSource == null) {
         this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.ICE_MAGIC), this, this.getOwner());
      }

      DamageSources.ignoreNextKnockback(target);
      target.hurt(this.damageSource, this.getDamage());
      int additionalFreezeTicks = 100;
      target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze(), target.getTicksFrozen() + additionalFreezeTicks));
   }

   public float getParticleCount() {
      return 0.15F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.of(HnSParticleHelper.ICE_CLOUD);
   }
}
