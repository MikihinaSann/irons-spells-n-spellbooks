package net.acetheeldritchking.discerning_the_eldritch.entity.spells.soul_eruption;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import it.crystalnest.prometheus.api.FireManager;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class SoulEruptionAoe extends AoeEntity {
   public static DamageSource DAMAGE_SOURCE;
   private int waveAnimTime = -1;

   public SoulEruptionAoe(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.reapplicationDelay = 25;
      this.setCircular();
   }

   public SoulEruptionAoe(Level level, float radius) {
      super((EntityType)DTEEntityRegistry.SOUL_FIRE_ERUPTION_AOE.get(), level);
      this.setRadius(radius);
   }

   public void applyEffect(LivingEntity target) {
      if (DAMAGE_SOURCE == null) {
         DAMAGE_SOURCE = SpellRegistries.SOUL_SLICE.get().getDamageSource(this.getOwner());
      }

      DamageSources.ignoreNextKnockback(target);
      target.hurt(DAMAGE_SOURCE, this.getDamage());
      FireManager.setOnFire(target, 25.0F, FireManager.SOUL_FIRE_TYPE);
      target.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.SCORCHED_SOUL_EFFECT, 500, 0, true, true, true));
   }

   public void tick() {
      float radius = this.getRadius();
      Level level = this.level();
      if (this.waveAnimTime++ < radius) {
         if (!level.isClientSide()) {
            if (this.waveAnimTime % 2 == 0) {
               float vol = (this.waveAnimTime + 8) / 16.0F;
               this.playSound((SoundEvent)DTESoundRegistry.SOUL_SLAM.get(), vol, Utils.random.nextIntBetweenInclusive(90, 110) * 0.01F);
            }

            float circMin = (this.waveAnimTime - 1) * 2 * 3.14F;
            float circMax = (this.waveAnimTime + 1) * 2 * 3.14F;
            int minBlocks = (int)Mth.clamp(circMin, 0.0F, 60.0F);
            int maxBlocks = (int)Mth.clamp(circMax, 0.0F, 60.0F);
            float anglePerBlockMin = 360.0F / minBlocks;
            float anglePerBlockMax = 360.0F / maxBlocks;

            for (int i = 0; i < minBlocks; i++) {
               Vec3 vec3 = new Vec3(this.waveAnimTime * Mth.cos(anglePerBlockMin * i), 0.0, this.waveAnimTime * Mth.sin(anglePerBlockMin * i));
               BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(vec3), 4)).below();
               Utils.createTremorBlock(level, blockPos, 0.1F + this.random.nextFloat() * 0.2F);
            }

            for (int i = 0; i < maxBlocks; i++) {
               Vec3 vec3 = new Vec3((this.waveAnimTime + 1) * Mth.cos(anglePerBlockMax * i), 0.0, (this.waveAnimTime + 1) * Mth.sin(anglePerBlockMax * i));
               BlockPos blockPos = BlockPos.containing(Utils.moveToRelativeGroundLevel(level, this.position().add(vec3), 4).add(0.0, 0.1, 0.0));
               if (level.getBlockState(blockPos.below()).isFaceSturdy(level, blockPos.below(), Direction.UP)) {
                  Utils.createTremorBlockWithState(level, Blocks.SOUL_FIRE.defaultBlockState(), blockPos, 0.1F + this.random.nextFloat() * 0.2F);
               }
            }

            List<LivingEntity> targets = level.getEntitiesOfClass(
               LivingEntity.class, this.getBoundingBox().inflate(this.getInflation().x, this.getInflation().y, this.getInflation().z)
            );
            int r1Sqr = this.waveAnimTime * this.waveAnimTime;
            int r2Sqr = (this.waveAnimTime + 1) * (this.waveAnimTime + 1);

            for (LivingEntity target : targets) {
               double distSqr = target.distanceToSqr(this);
               if (this.canHitEntity(target) && distSqr >= r1Sqr && distSqr <= r2Sqr && this.canHitTargetForGroundContext(target)) {
                  this.applyEffect(target);
               }
            }
         } else {
            int particles = (int)((this.waveAnimTime + 1) * 2 * 3.14F * 2.5F);
            float anglePerParticle = (float) (Math.PI * 2) / particles;

            for (int ix = 0; ix < particles; ix++) {
               Vec3 vec3 = new Vec3(Mth.cos(anglePerParticle * ix), 0.0, Mth.sin(anglePerParticle * ix));
               float r = Mth.lerp(Utils.random.nextFloat(), this.waveAnimTime, this.waveAnimTime + 1);
               Vec3 pos = vec3.scale(r).add(Utils.getRandomVec3(0.4)).add(this.position()).add(0.0, 0.5, 0.0);
               Vec3 motion = vec3.add(Utils.getRandomVec3(0.5)).scale(0.1);
               level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
               level.addParticle(ParticleTypes.SOUL, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
            }
         }
      } else {
         this.discard();
      }
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public boolean shouldBeSaved() {
      return false;
   }

   protected boolean canHitTargetForGroundContext(LivingEntity target) {
      return !this.level().noCollision(target.getBoundingBox().move(new Vec3(0.0, -0.9999, 0.0)));
   }

   protected Vec3 getInflation() {
      return new Vec3(0.0, 5.0, 0.0);
   }

   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 3.0F);
   }
}
