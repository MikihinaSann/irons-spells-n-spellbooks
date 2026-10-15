package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DragonEggEntity extends AbstractMagicProjectile {
   public DragonEggEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public DragonEggEntity(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends Projectile>)EntityRegistry.DRAGON_EGG_ENTITY.get(), pLevel);
      this.setOwner(owner);
   }

   public void trailParticles() {
      for (int i = 0; i < 1; i++) {
         Vec3 random = new Vec3(Utils.getRandomScaled(this.getBbWidth() * 0.5F), 0.0, Utils.getRandomScaled(this.getBbWidth() * 0.5F));
         this.level().addParticle(ParticleHelper.UNSTABLE_ENDER, this.getX() + random.x, this.getY() + 0.5, this.getZ() + random.z, 0.0, -0.05, 0.0);
      }
   }

   public void impactParticles(double x, double y, double z) {
   }

   private float horizontalDistanceSqr(LivingEntity livingEntity, Vec3 vec3) {
      double dx = livingEntity.getX() - vec3.x;
      double dz = livingEntity.getZ() - vec3.z;
      return (float)(dx * dx + dz * dz);
   }

   protected void onHitEntity(EntityHitResult pResult) {
      super.onHitEntity(pResult);
      if (!this.level().isClientSide) {
         Entity targetEntity = pResult.getEntity();
         DamageSources.applyDamage(targetEntity, this.getDamage(), PetSpellRegistry.MAYHEM.get().getDamageSource(this, this.getOwner()));
         targetEntity.invulnerableTime = 0;
         Vec3 targetArea = targetEntity.position();
         float radius = 3.0F;
         float radiusSqr = radius * radius;
         float damage = this.getDamage();
         SpellDamageSource source = PetSpellRegistry.MAYHEM.get().getDamageSource(this, this.getOwner());
         this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               new AABB(targetArea.subtract(radius, radius, radius), targetArea.add(radius, radius, radius)),
               livingEntity -> livingEntity != this.getOwner()
                  && this.horizontalDistanceSqr(livingEntity, targetArea) < radiusSqr
                  && livingEntity.isPickable()
                  && !DamageSources.isFriendlyFireBetween(livingEntity, this.getOwner())
                  && Utils.hasLineOfSight(this.level(), targetArea.add(0.0, 1.5, 0.0), livingEntity.getBoundingBox().getCenter(), true)
            )
            .forEach(livingEntity -> {
               DamageSources.applyDamage(livingEntity, damage, source);
               DamageSources.ignoreNextKnockback(livingEntity);
            });
         DragonCircleEntity dragonFire = new DragonCircleEntity(this.level());
         dragonFire.setOwner(this.getOwner());
         dragonFire.setDuration(100);
         dragonFire.setDamage(damage * 0.1F);
         dragonFire.setRadius(radius);
         dragonFire.setCircular();
         dragonFire.moveTo(targetArea);
         this.level().addFreshEntity(dragonFire);
         this.discard();
      }
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      if (!this.level().isClientSide) {
         Vec3 targetArea = blockHitResult.getBlockPos().getCenter();
         float radius = 3.0F;
         float radiusSqr = radius * radius;
         float damage = this.getDamage();
         SpellDamageSource source = PetSpellRegistry.MAYHEM.get().getDamageSource(this, this.getOwner());
         this.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               new AABB(targetArea.subtract(radius, radius, radius), targetArea.add(radius, radius, radius)),
               livingEntity -> livingEntity != this.getOwner()
                  && this.horizontalDistanceSqr(livingEntity, targetArea) < radiusSqr
                  && livingEntity.isPickable()
                  && !DamageSources.isFriendlyFireBetween(livingEntity, this.getOwner())
                  && Utils.hasLineOfSight(this.level(), targetArea.add(0.0, 1.5, 0.0), livingEntity.getBoundingBox().getCenter(), true)
            )
            .forEach(livingEntity -> {
               DamageSources.applyDamage(livingEntity, damage, source);
               DamageSources.ignoreNextKnockback(livingEntity);
            });
         DragonCircleEntity dragonFire = new DragonCircleEntity(this.level());
         dragonFire.setOwner(this.getOwner());
         dragonFire.setDuration(100);
         dragonFire.setDamage(damage * 0.1F);
         dragonFire.setRadius(radius);
         dragonFire.setCircular();
         dragonFire.moveTo(targetArea);
         this.level().addFreshEntity(dragonFire);
         this.discard();
      }
   }

   public void tick() {
      super.tick();
   }

   public float getSpeed() {
      return 1.0F;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean canBeHitByProjectile() {
      return true;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.DRAGON_FIREBALL_EXPLODE));
   }
}
