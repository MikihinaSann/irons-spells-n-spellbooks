package net.warphan.iss_magicfromtheeast.entity.spells.sword_dance;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESpellRegistries;
import net.warphan.iss_magicfromtheeast.util.MFTEParticleHelper;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JadeSword extends AbstractMagicProjectile implements GeoEntity {
   private int hitPerTicks;
   private int waitTimer = -1;
   private LivingEntity owner;
   BlockPos lastHitBlock;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public JadeSword(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public JadeSword(Level level, LivingEntity owner) {
      this((EntityType<? extends Projectile>)MFTEEntityRegistries.JADE_SWORD.get(), level);
      this.setOwner(owner);
      this.owner = owner;
      this.setDamage(this.damage);
   }

   public void trailParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement());
      this.level.addParticle(new DustParticleOptions(MFTESchoolRegistries.SYMMETRY.get().getTargetingColor(), 1.6F), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
      this.level.addParticle(ParticleTypes.GLOW, vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, MFTEParticleHelper.JADE_SHATTER, x, y, z, 1, 0.4, 0.4, 0.4, 0.2, true);
      MagicManager.spawnParticles(this.level, ParticleTypes.GLOW, x, y, z, 3, 0.4, 0.4, 0.4, 0.3, true);
   }

   public float getSpeed() {
      return 1.2F;
   }

   public void tick() {
      super.tick();
      this.hitPerTicks = 0;
      if (this.waitTimer > 0) {
         this.waitTimer--;
         if (this.waitTimer <= 110) {
            Vec3 stop = new Vec3(0.0, 0.0, 0.0);
            this.setDeltaMovement(stop);
            if (this.waitTimer <= 100 && this.waitTimer > 10) {
               LivingEntity target = this.owner.getLastHurtMob();
               if (target != null && !target.isDeadOrDying()) {
                  Vec3 attack = target.getBoundingBox().getCenter().subtract(this.position());
                  this.setDeltaMovement(this.getDeltaMovement().add(attack.multiply(1.0, 1.0, 1.0).normalize().scale(2.0)));
               }
            } else if (this.waitTimer == 5) {
               this.doBreaking();
            } else if (this.waitTimer == 0) {
               this.discard();
            }
         }
      }
   }

   public void setWaitTimer(int timerDeath) {
      this.waitTimer = timerDeath;
   }

   protected void onHitBlock(BlockHitResult bResult) {
   }

   private void doBreaking() {
      Vec3 center = this.position();
      MagicManager.spawnParticles(this.level, ParticleTypes.SCRAPE, center.x, center.y, center.z, 8, 0.8, 0.8, 0.8, 0.2, true);
      MagicManager.spawnParticles(this.level, MFTEParticleHelper.JADE_SHATTER, center.x, center.y, center.z, 3, 0.6, 0.6, 0.6, 0.2, true);
      this.level.playSound(null, BlockPos.containing(this.position()), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 2.0F, 0.5F);
   }

   public void onHitEntity(EntityHitResult entityHitResult) {
      Entity entity = entityHitResult.getEntity();
      DamageSources.applyDamage(entity, this.damage, MFTESpellRegistries.SWORD_DANCE_SPELL.get().getDamageSource(this, this.getOwner()));
      if (this.hitPerTicks++ < 5) {
         HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, x$0 -> this.canHitEntity(x$0));
         if (hitResult.getType() != Type.MISS && !EventHooks.onProjectileImpact(this, hitResult)) {
            this.onHit(hitResult);
         }
      }

      if (this.tickCount > 10) {
         entity.invulnerableTime = 0;
      }
   }

   public void onHit(HitResult result) {
      if (!this.level.isClientSide) {
         BlockPos blockPos = BlockPos.containing(result.getLocation());
         if (result.getType() == Type.BLOCK && !blockPos.equals(this.lastHitBlock)) {
            this.lastHitBlock = blockPos;
            this.discard();
         } else if (result.getType() == Type.ENTITY) {
            this.level
               .playSound(null, BlockPos.containing(this.position()), (SoundEvent)MFTESoundRegistries.JADE_SWORD_IMPACT.get(), SoundSource.NEUTRAL, 0.2F, 1.0F);
            if (this.tickCount > 10 && result.getType() == Type.ENTITY) {
               this.discard();
            }
         }
      }

      super.onHit(result);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public void onAntiMagic(MagicData pMagicData) {
   }

   public boolean shouldPierceShields() {
      return false;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
