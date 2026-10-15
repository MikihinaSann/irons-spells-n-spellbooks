package net.acetheeldritchking.discerning_the_eldritch.entity.spells.rift_walker;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.particle.DTEParticleHelper;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class UnstableRiftEntity extends AoeEntity {
   private Entity toTrack;
   public final int delay = 20;

   public UnstableRiftEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setCircular();
   }

   public UnstableRiftEntity(Level level, LivingEntity owner, float damage, float radius) {
      this((EntityType<? extends Projectile>)DTEEntityRegistry.UNSTABLE_RIFT.get(), level);
      this.setOwner(owner);
      this.setRadius(radius);
      this.setDamage(damage);
   }

   public void applyEffect(LivingEntity target) {
   }

   public void setTracking(Entity entity) {
      this.toTrack = entity;
   }

   public void tick() {
      if (this.toTrack != null && this.tickCount < 10) {
         this.setPos(this.toTrack.position());
      } else if (this.tickCount == 20) {
         this.playSound((SoundEvent)DTESoundRegistry.RIFT_WALKER_CAST.get(), 1.0F, Utils.random.nextIntBetweenInclusive(8, 12) * 0.1F);
         if (!this.level().isClientSide) {
            int r = 184;
            int g = 11;
            int b = 106;
            float rf = r / 255.0F;
            float gf = g / 255.0F;
            float bf = b / 255.0F;
            Vector3f colorRGB = new Vector3f(rf, gf, bf);
            Vec3 center = this.getBoundingBox().getCenter();
            MagicManager.spawnParticles(this.level(), DTEParticleHelper.RIFT_SLICE, center.x, center.y, center.z, 10, 0.0, 0.0, 0.0, 0.18, false);
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(colorRGB, this.getRadius() * 0.55F), center.x, center.y - 0.75, center.z, 1, 0.0, 0.0, 0.0, 0.0, true
            );
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(colorRGB, this.getRadius() * 0.9F), center.x, center.y, center.z, 1, 0.0, 0.0, 0.0, 0.0, true
            );
            MagicManager.spawnParticles(
               this.level(), new BlastwaveParticleOptions(colorRGB, this.getRadius() * 0.55F), center.x, center.y + 0.75, center.z, 1, 0.0, 0.0, 0.0, 0.0, true
            );
            float explosionRadius = this.getRadius();
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate(explosionRadius));
            Vec3 locCenter = Utils.moveToRelativeGroundLevel(this.level(), center, 2);
            locCenter = Utils.raycastForBlock(this.level(), locCenter, locCenter.add(0.0, 3.0, 0.0), Fluid.NONE).getLocation().add(locCenter).scale(0.5);

            for (Entity entity : entities) {
               double distSqr = entity.distanceToSqr(center);
               if (distSqr < explosionRadiusSqr
                  && this.canHitEntity(entity)
                  && Utils.hasLineOfSight(this.level(), locCenter, entity.getBoundingBox().getCenter(), false)) {
               }

               double p = Mth.clamp(1.0 - distSqr / explosionRadiusSqr + 0.4F, 0.0, 1.0);
               float damage = (float)(this.damage * p);
               DamageSources.applyDamage(entity, damage, SpellRegistries.RIFT_WALKER.get().getDamageSource(this, this.getOwner()));
            }
         }
      } else if (this.tickCount > 20) {
         this.discard();
      }

      if (this.level().isClientSide && this.tickCount < 10) {
         Vec3 pos = this.getBoundingBox().getCenter();

         for (int i = 0; i < 3; i++) {
            Vec3 vec3 = Utils.getRandomVec3(1.0);
            vec3 = vec3.multiply(vec3).multiply(Mth.sign(vec3.x), Mth.sign(vec3.y), Mth.sign(vec3.z)).scale(this.getRadius()).add(pos);
            Vec3 motion = this.position().subtract(vec3).scale(0.125);
            this.level().addParticle(ParticleTypes.SMOKE, vec3.x, vec3.y - 0.5, vec3.z, motion.x, motion.y, motion.z);
         }
      }
   }

   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return true;
   }

   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }
}
