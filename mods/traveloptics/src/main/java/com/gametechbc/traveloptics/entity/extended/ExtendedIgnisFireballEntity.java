package com.gametechbc.traveloptics.entity.extended;

import com.gametechbc.traveloptics.init.TOEntities;
import com.github.L_Ender.cataclysm.entity.effect.Cm_Falling_Block_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Abyss_Fireball_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Ignis_Fireball_Entity;
import com.github.L_Ender.cataclysm.init.ModEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.EntityHitResult;

public class ExtendedIgnisFireballEntity extends Ignis_Fireball_Entity {
   private float customDamage;

   public ExtendedIgnisFireballEntity(EntityType<? extends ExtendedIgnisFireballEntity> type, Level level) {
      super(type, level);
   }

   public ExtendedIgnisFireballEntity(Level level, LivingEntity entity, double x, double y, double z) {
      super(level, entity, x, y, z);
      this.customDamage = 1.0F;
   }

   public ExtendedIgnisFireballEntity(Level worldIn, LivingEntity entity) {
      this((EntityType<? extends ExtendedIgnisFireballEntity>)TOEntities.EXTENDED_IGNIS_FIREBALL.get(), worldIn);
      this.setOwner(entity);
      this.customDamage = 1.0F;
   }

   public void setCustomDamage(float damage) {
      this.customDamage = damage;
   }

   public float getCustomDamage() {
      return this.customDamage;
   }

   protected void onHitEntity(EntityHitResult result) {
      super.onHitEntity(result);
      Entity entity = result.getEntity();
      Entity shooter = this.getOwner();
      if (this.level() instanceof ServerLevel serverLevel
         && !(entity instanceof Ignis_Fireball_Entity)
         && !(entity instanceof Ignis_Abyss_Fireball_Entity)
         && !(entity instanceof Cm_Falling_Block_Entity)
         && this.getFired()) {
         boolean flag;
         if (shooter instanceof LivingEntity owner) {
            DamageSource damageSource = this.damageSources().mobProjectile(this, owner);
            float damage = this.isSoul() ? this.getCustomDamage() + 2.0F : this.getCustomDamage();
            if (entity instanceof LivingEntity target) {
               flag = target.hurt(damageSource, damage + target.getMaxHealth() * 0.07F);
            } else {
               flag = entity.hurt(damageSource, damage);
            }

            if (flag) {
               if (entity.isAlive()) {
                  EnchantmentHelper.doPostAttackEffects(serverLevel, entity, damageSource);
               }

               owner.heal(5.0F);
            }
         } else {
            flag = entity.hurt(this.damageSources().magic(), this.getCustomDamage());
         }

         if (flag && entity instanceof LivingEntity target) {
            MobEffectInstance effectInstance = target.getEffect(ModEffect.EFFECTBLAZING_BRAND);
            int i = 2;
            if (effectInstance != null) {
               i += effectInstance.getAmplifier();
               target.removeEffectNoUpdate(ModEffect.EFFECTBLAZING_BRAND);
            } else {
               i--;
            }

            i = Mth.clamp(i, 0, 4);
            target.addEffect(new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND, 200, i, false, false, true));
         }
      }

      this.level().explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, true, ExplosionInteraction.NONE);
      this.discard();
   }

   public void tick() {
      super.tick();
   }
}
