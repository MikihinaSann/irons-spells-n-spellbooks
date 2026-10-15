package net.acetheeldritchking.cataclysm_spellbooks.entity.spells.blazing_aoe;

import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class BlazingAoE extends AoeEntity {
   public static DamageSource DAMAGE_SOURCE;

   public BlazingAoE(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public BlazingAoE(Level level) {
      this((EntityType<? extends Projectile>)CSEntityRegistry.BLAZING_AOE_ENTITY.get(), level);
   }

   public void applyEffect(LivingEntity target) {
      if (DAMAGE_SOURCE == null) {
         DAMAGE_SOURCE = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.FIRE_MAGIC), this, this.getOwner());
      }

      DamageSources.ignoreNextKnockback(target);
      target.hurt(DAMAGE_SOURCE, this.getDamage());
      target.addEffect(new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND, 100, 0));
   }

   public float getParticleCount() {
      return 0.2F * this.getRadius();
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.of((ParticleOptions)ModParticle.TRAP_FLAME.get());
   }
}
