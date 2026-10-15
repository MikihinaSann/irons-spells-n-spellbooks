package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.projectile.Storm_Serpent_Entity;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.aqua.SeaSerpentSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SpellStormSerpentEntity extends Storm_Serpent_Entity {
   private static Field WARMUP_FIELD;
   private final Set<Integer> hitEntities = new HashSet<>();

   public SpellStormSerpentEntity(EntityType<? extends Storm_Serpent_Entity> type, Level level) {
      super(type, level);
   }

   public SpellStormSerpentEntity(
      EntityType<? extends Storm_Serpent_Entity> type,
      Level level,
      double x,
      double y,
      double z,
      float yaw,
      int delay,
      LivingEntity caster,
      float damage,
      boolean right
   ) {
      super(type, level);
      this.setPos(x, y, z);
      this.setYRot(yaw * (180.0F / (float)Math.PI));
      this.setCaster(caster);
      this.setDamage(damage);
      this.setRight(right);

      try {
         if (WARMUP_FIELD != null) {
            WARMUP_FIELD.setInt(this, delay);
         }
      } catch (Exception var15) {
         var15.printStackTrace();
      }
   }

   protected void damage(LivingEntity target) {
      if (target != null && target.isAlive()) {
         if (this.hitEntities.contains(target.getId())) {
            return;
         }

         this.hitEntities.add(target.getId());
         target.invulnerableTime = 0;
         if (this.getCaster() != null) {
            DamageSources.applyDamage(target, this.getDamage(), ((SeaSerpentSpell)ModSpells.SEA_SERPENT.get()).getDamageSource(this, this.getCaster()));
         } else {
            super.damage(target);
         }

         target.invulnerableTime = 0;
      }
   }

   static {
      try {
         WARMUP_FIELD = Storm_Serpent_Entity.class.getDeclaredField("warmupDelayTicks");
         WARMUP_FIELD.setAccessible(true);
      } catch (Exception var1) {
         var1.printStackTrace();
      }
   }
}
