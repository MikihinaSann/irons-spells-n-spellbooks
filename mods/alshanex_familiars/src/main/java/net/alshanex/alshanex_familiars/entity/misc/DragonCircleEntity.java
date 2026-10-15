package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class DragonCircleEntity extends AoeEntity {
   private DamageSource damageSource;

   public DragonCircleEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public DragonCircleEntity(Level level) {
      this((EntityType<? extends Projectile>)EntityRegistry.DRAGON_CIRCLE.get(), level);
   }

   public void applyEffect(LivingEntity target) {
      if (this.damageSource == null) {
         this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.FIRE_FIELD), this, this.getOwner());
      }

      DamageSources.ignoreNextKnockback(target);
      target.hurt(this.damageSource, this.getDamage());
   }

   public float getParticleCount() {
      return 1.2F * this.getRadius();
   }

   protected float particleYOffset() {
      return 0.25F;
   }

   protected float getParticleSpeedModifier() {
      return 1.4F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.of(ParticleTypes.DRAGON_BREATH);
   }
}
