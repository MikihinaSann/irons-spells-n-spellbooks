package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.lightning.LightningBallSpell;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightning;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class LightningBallProjectile extends BallLightning {
   private float explosionRadius;

   public LightningBallProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public LightningBallProjectile(Level level, LivingEntity shooter) {
      super((EntityType)ModEntityTypes.LIGHTNING_BALL_PROJECTILE.get(), level);
      this.setOwner(shooter);
   }

   public void setExplosionRadius(float radius) {
      this.explosionRadius = radius;
   }

   public float getExplosionRadius() {
      return this.explosionRadius;
   }

   public float getSpeed() {
      return 1.15F;
   }

   protected void onHit(HitResult hitResult) {
      if (!this.level().isClientSide) {
         Vec3 impactPos = hitResult.getLocation();
         MagicManager.spawnParticles(
            this.level(),
            ParticleHelper.ELECTRIC_SPARKS,
            impactPos.x,
            impactPos.y,
            impactPos.z,
            50,
            this.getExplosionRadius() * 0.5,
            this.getExplosionRadius() * 0.5,
            this.getExplosionRadius() * 0.5,
            0.5,
            true
         );
         Vector3f edge = new Vector3f(0.7F, 1.0F, 1.0F);
         Vector3f center = new Vector3f(1.0F, 1.0F, 1.0F);
         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(edge, this.getExplosionRadius() * 1.02F),
            impactPos.x,
            impactPos.y + 0.15F,
            impactPos.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(edge, this.getExplosionRadius() * 0.98F),
            impactPos.x,
            impactPos.y + 0.15F,
            impactPos.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(center, this.getExplosionRadius()),
            impactPos.x,
            impactPos.y + 0.165F,
            impactPos.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         MagicManager.spawnParticles(
            this.level(),
            new BlastwaveParticleOptions(center, this.getExplosionRadius()),
            impactPos.x,
            impactPos.y + 0.135F,
            impactPos.z,
            1,
            0.0,
            0.0,
            0.0,
            0.0,
            true
         );
         MagicManager.spawnParticles(
            this.level(),
            ParticleHelper.ELECTRICITY,
            impactPos.x,
            impactPos.y + 1.0,
            impactPos.z,
            80,
            0.25,
            0.25,
            0.25,
            0.7F + this.getExplosionRadius() * 0.1F,
            false
         );
         CameraShakeManager.addCameraShake(new CameraShakeData(this.level(), 30, impactPos, this.getExplosionRadius() * 2.0F));
         float explosionRadius = this.getExplosionRadius();
         float explosionRadiusSqr = explosionRadius * explosionRadius;
         List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
         Vec3 losPoint = Utils.raycastForBlock(this.level(), this.position(), this.position().add(0.0, 2.0, 0.0), Fluid.NONE).getLocation();
         Vec3 start = impactPos;

         for (Entity entity : entities) {
            double distanceSqr = entity.distanceToSqr(impactPos);
            if (distanceSqr < explosionRadiusSqr
               && this.canHitEntity(entity)
               && Utils.hasLineOfSight(this.level(), losPoint, entity.getBoundingBox().getCenter(), true)) {
               double p = 1.0 - distanceSqr / explosionRadiusSqr;
               float damage = (float)(this.getDamage() * p);
               boolean damaged = DamageSources.applyDamage(
                  entity, damage, ((LightningBallSpell)ModSpells.LIGHTNING_BALL.get()).getDamageSource(this, this.getOwner())
               );
               if (entity instanceof LivingEntity living && this.level() instanceof ServerLevel serverLevel) {
                  if (damaged) {
                     living.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 100, 0, true, true, true));
                  }

                  Vec3 targetPos = living.getBoundingBox().getCenter();
                  serverLevel.sendParticles(new ZapParticleOption(targetPos), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
                  if (this.level().random.nextFloat() < 0.6F) {
                     Vec3 randomDir = Utils.getRandomVec3(1.0);
                     Vec3 randomDest = start.add(randomDir.scale(3.0F + explosionRadius * 0.5F).add(0.0, 2.0F + this.level().random.nextFloat() * 2.0F, 0.0));
                     serverLevel.sendParticles(new ZapParticleOption(randomDest), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
                  }
               }
            }
         }

         for (int i = 0; i < 6 + (int)(explosionRadius * 0.8F); i++) {
            Vec3 randomDest = start.add(Utils.getRandomVec3(4.0F + explosionRadius * 0.6F).add(0.0, 3.0F + this.level().random.nextFloat() * 3.0F, 0.0));
            ((ServerLevel)this.level()).sendParticles(new ZapParticleOption(randomDest), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
         }

         if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), this.getExplosionRadius() / 2.0F, ExplosionInteraction.BLOCK);
         }

         this.playSound(
            (SoundEvent)SoundRegistry.CHAIN_LIGHTNING_CHAIN.get(),
            4.0F,
            (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F
         );
         this.discard();
      }
   }
}
