package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.ball_lightning.BallLightning;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MegidoEntity extends AbstractMagicProjectile {
   List<Entity> lastVictims;
   Entity initialVictim;
   public int maxConnections = Integer.MAX_VALUE;
   public float range = 5.0F;
   private static final Supplier<AbstractSpell> SPELL = SpellRegistry.CHAIN_LIGHTNING_SPELL;
   int hits;

   public MegidoEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.lastVictims = new ArrayList<>();
      this.setNoGravity(true);
      this.noPhysics = true;
   }

   public MegidoEntity(Level level, Entity owner, Entity initialVictim) {
      this((EntityType<? extends Projectile>)EntityRegistry.CHAIN_LIGHTNING.get(), level);
      this.setOwner(owner);
      this.setPos(initialVictim.position());
      this.initialVictim = initialVictim;
   }

   public void tick() {
      super.tick();
      int f = this.tickCount - 1;
      if (!this.level().isClientSide && f % 6 == 0) {
         if (f == 0) {
            this.doHurt(this.initialVictim);
            if (this.getOwner() != null) {
               Vec3 start = this.getOwner().position().add(0.0, this.getOwner().getBbHeight() / 2.0F, 0.0);
               Vec3 dest = this.initialVictim.position().add(0.0, this.initialVictim.getBbHeight() / 2.0F, 0.0);
               ((ServerLevel)this.level()).sendParticles(new ZapParticleOption(dest), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
         } else if (f < 70) {
            Entity entity = this.lastVictims.get(this.lastVictims.size() - 1);
            Entity victim;
            if (entity instanceof BallLightning) {
               victim = this.getClosestEntity(entity, this.range);
            } else {
               victim = this.getRandomBall(entity, this.range);
            }

            if (victim != null && this.hits < this.maxConnections && victim.distanceToSqr(entity) < this.range * this.range) {
               this.doHurt(victim);
               victim.playSound((SoundEvent)SoundRegistry.CHAIN_LIGHTNING_CHAIN.get(), 2.0F, 1.0F);
               Vec3 start = new Vec3(entity.xOld, entity.yOld, entity.zOld).add(0.0, entity.getBbHeight() / 2.0F, 0.0);
               Vec3 dest = victim.position().add(0.0, victim.getBbHeight() / 2.0F, 0.0);
               ((ServerLevel)this.level()).sendParticles(new ZapParticleOption(dest), start.x, start.y, start.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
         }
      }
   }

   public Entity getClosestEntity(Entity baseEntity, double range) {
      List<Entity> nearbyEntities = baseEntity.level()
         .getEntities(baseEntity, baseEntity.getBoundingBox().inflate(range), entity -> entity != baseEntity && this.canHitEntity(entity));
      Random random = new Random();
      Entity closestNonBallLightning = nearbyEntities.stream()
         .filter(entity -> !(entity instanceof BallLightning))
         .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(baseEntity)))
         .orElse(null);
      if (closestNonBallLightning == null) {
         List<Entity> ballLightnings = nearbyEntities.stream().filter(entity -> entity instanceof BallLightning).toList();
         if (!ballLightnings.isEmpty()) {
            return ballLightnings.get(random.nextInt(ballLightnings.size()));
         }
      }

      return closestNonBallLightning;
   }

   public Entity getRandomBall(Entity baseEntity, double range) {
      List<Entity> nearbyEntities = baseEntity.level()
         .getEntities(baseEntity, baseEntity.getBoundingBox().inflate(range), entity -> entity != baseEntity && this.canHitEntity(entity));
      Random random = new Random();
      List<Entity> ballLightnings = nearbyEntities.stream().filter(entity -> entity instanceof BallLightning).toList();
      return !ballLightnings.isEmpty()
         ? ballLightnings.get(random.nextInt(ballLightnings.size()))
         : nearbyEntities.stream()
            .filter(entity -> !(entity instanceof BallLightning))
            .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(baseEntity)))
            .orElse(null);
   }

   public void doHurt(Entity victim) {
      this.hits++;
      if (!(victim instanceof BallLightning)) {
         DamageSources.applyDamage(victim, this.damage, SPELL.get().getDamageSource(this, this.getOwner()));
      }

      MagicManager.spawnParticles(
         this.level(),
         ParticleHelper.ELECTRICITY,
         victim.getX(),
         victim.getY() + victim.getBbHeight() / 2.0F,
         victim.getZ(),
         10,
         victim.getBbWidth() / 3.0F,
         victim.getBbHeight() / 3.0F,
         victim.getBbWidth() / 3.0F,
         0.1,
         false
      );
      this.lastVictims.add(victim);
   }

   protected boolean canHitEntity(Entity target) {
      return target instanceof LivingEntity
            && !DamageSources.isFriendlyFireBetween(target, this.getOwner())
            && target != this.getOwner()
            && super.canHitEntity(target)
         || target instanceof BallLightning;
   }

   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 0.0F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY));
   }

   public boolean shouldBeSaved() {
      return false;
   }
}
