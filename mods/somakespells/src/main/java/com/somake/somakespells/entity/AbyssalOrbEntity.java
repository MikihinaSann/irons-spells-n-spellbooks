package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AbyssalOrbEntity extends AbstractMagicProjectile {
   public AbyssalOrbEntity(EntityType<? extends AbstractMagicProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public AbyssalOrbEntity(Level level, LivingEntity owner) {
      this((EntityType<? extends AbstractMagicProjectile>)ModEntityTypes.ABYSSAL_ORB.get(), level);
      this.setOwner(owner);
   }

   public void trailParticles() {
      if (this.level().isClientSide) {
         Vec3 vec3 = this.getDeltaMovement();
         this.level().addParticle(ParticleTypes.DRAGON_BREATH, this.getX(), this.getY() + 0.5, this.getZ(), -vec3.x * 0.5, -vec3.y * 0.5, -vec3.z * 0.5);
         this.level().addParticle(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 0.5, this.getZ(), -vec3.x * 0.5, -vec3.y * 0.5, -vec3.z * 0.5);
      }
   }

   public void impactParticles(double x, double y, double z) {
      if (this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 60, 1.5, 1.5, 1.5, 0.2);
         serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 40, 1.5, 1.5, 1.5, 0.2);
      }
   }

   public float getSpeed() {
      return 1.5F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundEvents.GENERIC_EXPLODE);
   }

   protected void onHit(HitResult hitResult) {
      super.onHit(hitResult);
      if (!this.level().isClientSide) {
         float explosionRadius = 3.5F;
         MagicManager.spawnParticles(
            this.level(), new BlastwaveParticleOptions(0.6F, 0.0F, 0.8F, explosionRadius), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
         );
         this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius)).forEach(entity -> {
            if (entity != this.getOwner() && entity.distanceToSqr(this) < explosionRadius * explosionRadius) {
               entity.invulnerableTime = 0;
               DamageSources.applyDamage(entity, this.getDamage(), this.damageSources().mobAttack((LivingEntity)this.getOwner()));
            }
         });
         this.discard();
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }
}
