package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Spark_Entity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class CustomSparkEntity extends Spark_Entity {
   private boolean shouldChain = false;
   private AbstractSpell spell;

   public void setShouldChain(boolean shouldChain) {
      this.shouldChain = shouldChain;
   }

   public void setSpell(AbstractSpell spell) {
      this.spell = spell;
   }

   public CustomSparkEntity(EntityType<? extends Spark_Entity> type, Level world) {
      super(type, world);
   }

   public CustomSparkEntity(LivingEntity thrower, Level level) {
      super((EntityType)ModEntityTypes.CUSTOM_SPARK_ENTITY.get(), level);
      this.setOwner(thrower);
      this.setPos(thrower.getX(), thrower.getEyeY() - 0.1, thrower.getZ());
   }

   protected void onHitBlock(BlockHitResult result) {
      if (!this.level().isClientSide) {
         MagicManager.spawnParticles(
            this.level(), new BlastwaveParticleOptions(0.0F, 0.75F, 1.0F, 3.0F), this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0, true
         );
         float radius = this.getAreaRadius();
         if (radius <= 0.0F) {
            radius = 3.0F;
         }

         CameraShakeManager.addCameraShake(new CameraShakeData(this.level(), 30, this.position(), radius * 2.0F));
         float damage = this.getAreaDamage();
         if (radius <= 0.0F) {
            radius = 3.0F;
         }

         if (damage <= 0.0F) {
            damage = 5.0F;
         }

         float finalDamage = damage;
         this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(radius)).forEach(entity -> {
            if (entity != this.getOwner()) {
               entity.invulnerableTime = 0;
               boolean damaged;
               if (this.spell != null) {
                  damaged = entity.hurt(this.spell.getDamageSource(this, this.getOwner()), finalDamage);
               } else {
                  damaged = entity.hurt(this.damageSources().lightningBolt(), finalDamage);
               }

               if (damaged) {
                  entity.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 100, 0, true, true, true));
               }
            }
         });
         if (this.getOwner() instanceof LivingEntity entity1) {
            this.level()
               .addFreshEntity(new Lightning_Storm_Entity(this.level(), this.getX(), this.getY(), this.getZ(), this.getYRot(), -9, 0.0F, 0.0F, entity1, 2.0F));
            if (this.shouldChain) {
               LivingEntity target = this.level()
                  .getNearestEntity(
                     LivingEntity.class,
                     TargetingConditions.forCombat().range(20.0).selector(e -> e != this.getOwner()),
                     entity1,
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     this.getBoundingBox().inflate(20.0)
                  );
               if (target != null) {
                  CustomSparkEntity chainSpark = new CustomSparkEntity(entity1, this.level());
                  chainSpark.setPos(this.getX(), this.getY() + 0.5, this.getZ());
                  chainSpark.setDamage(this.getDamage());
                  chainSpark.setAreaDamage(this.getAreaDamage());
                  chainSpark.setAreaRadius(this.getAreaRadius());
                  chainSpark.setShouldChain(false);
                  double dx = target.getX() - this.getX();
                  double dy = target.getY() - this.getY();
                  double dz = target.getZ() - this.getZ();
                  chainSpark.shoot(dx, dy + 1.0, dz, 0.6F, 0.0F);
                  this.level().addFreshEntity(chainSpark);
               }
            }
         }

         this.discard();
      }
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      if (!this.level().isClientSide && result.getEntity() instanceof LivingEntity livingTarget && livingTarget != this.getOwner()) {
         livingTarget.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 100, 0, true, true, true));
      }
   }
}
