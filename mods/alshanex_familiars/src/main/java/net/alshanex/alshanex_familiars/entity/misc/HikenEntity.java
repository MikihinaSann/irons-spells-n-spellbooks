package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent.Start;
import net.neoforged.neoforge.network.PacketDistributor;

public class HikenEntity extends AbstractMagicProjectile {
   public HikenEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public HikenEntity(Level pLevel, LivingEntity pShooter) {
      this((EntityType<? extends Projectile>)EntityRegistry.HIKEN_ENTITY.get(), pLevel);
      this.setOwner(pShooter);
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 1.15F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(Holder.direct((SoundEvent)SoundEvents.GENERIC_EXPLODE.value()));
   }

   protected void onHit(HitResult hitResult) {
      if (!this.level().isClientSide) {
         this.impactParticles(this.xOld, this.yOld, this.zOld);
         if (hitResult.getType() == Type.BLOCK) {
            if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
               Explosion explosion = new Explosion(
                  this.level(),
                  null,
                  PetSpellRegistry.HIKEN.get().getDamageSource(this, this.getOwner()),
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  this.getExplosionRadius() / 2.0F,
                  true,
                  BlockInteraction.DESTROY,
                  ParticleTypes.EXPLOSION,
                  ParticleTypes.EXPLOSION_EMITTER,
                  SoundEvents.GENERIC_EXPLODE
               );
               if (!((Start)NeoForge.EVENT_BUS.post(new Start(this.level(), explosion))).isCanceled()) {
                  explosion.explode();
                  explosion.finalizeExplosion(false);
               }
            }

            for (LivingEntity targetEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.0))) {
               if (targetEntity != this.getOwner() && !this.getOwner().isAlliedTo(targetEntity) && targetEntity.isAlive()) {
                  targetEntity.hurt(PetSpellRegistry.HIKEN.get().getDamageSource(this, this.getOwner()), this.getDamage());
                  Vec3 knockbackDirection = targetEntity.position().subtract(this.position()).normalize().scale(1.0);
                  targetEntity.setDeltaMovement(targetEntity.getDeltaMovement().add(knockbackDirection));
                  targetEntity.setRemainingFireTicks(200);
               }
            }

            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
               this.getOwner(),
               new FieryExplosionParticlesPacket(new Vec3(this.getX(), this.getY() + 0.15F, this.getZ()), this.getExplosionRadius()),
               new CustomPacketPayload[0]
            );
            this.playSound(SoundEvents.BLAZE_SHOOT, 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            this.discard();
         } else if (hitResult.getType() == Type.ENTITY) {
            for (LivingEntity targetEntityx : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0))) {
               if (targetEntityx != this.getOwner() && !this.getOwner().isAlliedTo(targetEntityx) && targetEntityx.isAlive()) {
                  targetEntityx.hurt(PetSpellRegistry.HIKEN.get().getDamageSource(this, this.getOwner()), this.getDamage());
                  Vec3 knockbackDirection = targetEntityx.position().subtract(this.position()).normalize().scale(1.0);
                  targetEntityx.setDeltaMovement(targetEntityx.getDeltaMovement().add(knockbackDirection));
                  targetEntityx.setRemainingFireTicks(200);
               }
            }
         }
      }
   }

   public void trailParticles() {
      Vec3 vec3 = this.getDeltaMovement();
      double d0 = this.getX() - vec3.x;
      double d1 = this.getY() - vec3.y;
      double d2 = this.getZ() - vec3.z;
      int count = Mth.clamp((int)(vec3.lengthSqr() * 4.0), 1, 4);

      for (int i = 0; i < count; i++) {
         Vec3 random = Utils.getRandomVec3(0.25);
         float f = (float)i / count;
         double x = Mth.lerp(f, d0, this.getX());
         double y = Mth.lerp(f, d1, this.getY());
         double z = Mth.lerp(f, d2, this.getZ());
         this.level().addParticle(ParticleHelper.EMBERS, x - random.x, y + 0.5 - random.y, z - random.z, random.x * 0.5, random.y * 0.5, random.z * 0.5);
      }
   }

   public void tick() {
      super.tick();
      this.createParticleSphere();
   }

   private void createParticleSphere() {
      double radius = this.getBbWidth() + 1.0F;
      int particleCount = 15;

      for (int i = 0; i < particleCount; i++) {
         double theta = Math.toRadians(this.level().random.nextDouble() * 360.0);
         double phi = Math.toRadians(this.level().random.nextDouble() * 180.0);
         double randomRadius = radius * Math.pow(this.level().random.nextDouble(), 0.3333333333333333);
         double xOffset = randomRadius * Math.sin(phi) * Math.cos(theta);
         double yOffset = randomRadius * Math.cos(phi);
         double zOffset = randomRadius * Math.sin(phi) * Math.sin(theta);
         this.level().addParticle(ParticleHelper.FIRE, this.getX() + xOffset, this.getY() + yOffset, this.getZ() + zOffset, 0.0, 0.0, 0.0);
      }
   }
}
