package net.hazen.hazennstuff.Entity.Spells.Nature.ThornChakram;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.hazen.hazennstuff.Registries.HnSEntityRegistry;
import net.hazen.hazennstuff.Registries.HnSParticleHelper;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.HnSSpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ThornChakram extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean hasHitOnce = false;
   private LivingEntity currentTarget = null;
   private final int maxHits = 3;
   private int hitCount = 0;
   int bounces;
   HashMap<UUID, Integer> victims;
   private final RawAnimation idle = RawAnimation.begin().thenLoop("animation.thorn_chakram_spell.idle");

   public ThornChakram(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.victims = new HashMap<>();
      this.setNoGravity(true);
   }

   public ThornChakram(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)HnSEntityRegistry.THORN_CHAKRAM.get(), level);
      this.setOwner(shooter);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      Vec3 random = Utils.getRandomVec3(0.0);
      pos = pos.add(this.getDeltaMovement());
      this.level.addParticle(HnSParticleHelper.LEAF_PARTICLE, pos.x, pos.y, pos.z, random.x, random.y, random.z);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, HnSParticleHelper.LEAF_PARTICLE, x, y, z, 12, 0.08, 0.08, 0.08, 0.3, false);
   }

   public float getSpeed() {
      return 1.2F;
   }

   protected boolean canHitEntity(Entity pTarget) {
      return super.canHitEntity(pTarget) && this.canHitVictim(pTarget);
   }

   public void tick() {
      super.tick();
      if (this.currentTarget != null && this.currentTarget.isAlive()) {
         Vec3 direction = this.currentTarget.position().add(0.0, this.currentTarget.getBbHeight() * 0.5, 0.0).subtract(this.position()).normalize();
         double speed = this.getDeltaMovement().length();
         this.setDeltaMovement(direction.scale(speed));
      }

      this.handleHitDetection();
   }

   public void handleHitDetection() {
      Vec3 vec3 = this.getDeltaMovement();
      Vec3 pos = this.position();
      Vec3 vec32 = pos.add(vec3);
      HitResult hitresult = this.level.clip(new ClipContext(pos, vec32, Block.COLLIDER, Fluid.NONE, this));
      if (hitresult.getType() != Type.MISS) {
         this.onHit(hitresult);
      } else {
         for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(0.0), this::canHitEntity)) {
            if (entity instanceof LivingEntity living && !entity.isSpectator()) {
               this.onHit(new EntityHitResult(entity, this.position()));
               this.hasHitOnce = true;
               this.acquireNextTarget(living);
               return;
            }
         }
      }
   }

   private void acquireNextTarget(LivingEntity lastHit) {
      List<LivingEntity> nearby = this.level
         .getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8.0), e -> e != lastHit && e.isAlive() && this.canHitEntity(e));
      if (!nearby.isEmpty()) {
         nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this)));
         this.currentTarget = nearby.get(0);
      }
   }

   public boolean canHitVictim(Entity entity) {
      Integer timestamp = this.victims.get(entity.getUUID());
      return timestamp == null || entity.tickCount - timestamp >= 10;
   }

   protected void onHitEntity(EntityHitResult pResult) {
      super.onHitEntity(pResult);
      Entity target = pResult.getEntity();
      if (target instanceof LivingEntity livingEntity) {
         DamageSources.ignoreNextKnockback(livingEntity);
      }

      DamageSources.applyDamage(target, this.getDamage(), HnSSpellRegistries.THORN_CHAKRAM.get().getDamageSource(this, this.getOwner()));
      this.victims.put(target.getUUID(), target.tickCount);
      this.hitCount++;
      if (this.hitCount >= 3) {
         this.discard();
      }
   }

   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
      switch (pResult.getDirection()) {
         case UP:
         case DOWN:
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, this.isNoGravity() ? -1.0 : -0.8F, 1.0));
            break;
         case EAST:
         case WEST:
            this.setDeltaMovement(this.getDeltaMovement().multiply(-1.0, 1.0, 1.0));
            break;
         case NORTH:
         case SOUTH:
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 1.0, -1.0));
      }

      if (++this.bounces >= 6) {
         this.discard();
      }
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(HnSSounds.TERRARIA_IMPACT);
   }

   private PlayState predicate(AnimationState event) {
      event.getController().setAnimation(this.idle);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllerRegistrar) {
      controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
