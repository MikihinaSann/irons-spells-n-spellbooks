package com.snackpirate.aeromancy.spells.thunderclap;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class ShockwaveEntity extends AbstractMagicProjectile implements AntiMagicSusceptible {
   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 0.0F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   protected ShockwaveEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public void onAntiMagic(MagicData playerMagicData) {
      super.onAntiMagic(playerMagicData);
   }

   protected void defineSynchedData(Builder builder) {
   }
}
