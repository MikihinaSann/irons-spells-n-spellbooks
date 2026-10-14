package com.gametechbc.traveloptics.entity.spells;

import com.gametechbc.traveloptics.api.particle.AdvancedCylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.init.TOEntities;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class AerialCollapseVisualEntity extends AoeEntity implements AntiMagicSusceptible {
   private int tickCounter;

   public AerialCollapseVisualEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public AerialCollapseVisualEntity(Level level) {
      this((EntityType<? extends Projectile>)TOEntities.AERIAL_COLLAPSE_VISUAL_ENTITY.get(), level);
   }

   public void tick() {
      super.tick();
      this.tickCounter++;
      if (!this.level().isClientSide) {
         AdvancedCylinderParticleManager.spawnParticles(
            this.level(),
            this.position(),
            (int)this.getRadius(),
            ParticleHelper.ACID_BUBBLE,
            ParticleDirection.UPWARD,
            this.getRadius(),
            17.0,
            0.0,
            0.0,
            0.0,
            1.0,
            false
         );
      }

      if (!this.level().isClientSide && this.tickCounter % 13 == 0) {
         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.NATURE.get()).getTargetingColor(), this.getRadius()),
            this.getX(),
            this.getY() + 0.165F,
            this.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            false
         );
      }

      if (!this.level().isClientSide && this.tickCount >= 55) {
         this.discard();
      }
   }

   public void applyEffect(LivingEntity target) {
   }

   public float getParticleCount() {
      return 0.0F;
   }

   protected float particleYOffset() {
      return 0.25F;
   }

   protected float getParticleSpeedModifier() {
      return 1.4F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void onAntiMagic(MagicData magicData) {
   }
}
