package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.Lightning_Area_Effect_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Water_Spear_Entity;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellWaterSpearEntity extends Water_Spear_Entity {
   private AbstractSpell damageSpell;

   public SpellWaterSpearEntity(EntityType<? extends Water_Spear_Entity> type, Level level) {
      super(type, level);
   }

   public SpellWaterSpearEntity(
      EntityType<? extends Water_Spear_Entity> type, LivingEntity caster, double x, double y, double z, Vec3 dir, float damage, Level level
   ) {
      super(type, caster, x, y, z, dir, damage, level);
   }

   public void setDamageSpell(AbstractSpell damageSpell) {
      if (damageSpell != null) {
         this.damageSpell = damageSpell;
      }
   }

   protected boolean canHitEntity(Entity entity) {
      return !(entity instanceof Lightning_Storm_Entity) && !(entity instanceof Lightning_Area_Effect_Entity) ? super.canHitEntity(entity) : false;
   }

   protected void onHitEntity(EntityHitResult result) {
      if (!this.level().isClientSide) {
         Entity owner = this.getOwner();
         AbstractSpell spell = this.damageSpell != null ? this.damageSpell : (AbstractSpell)ModSpells.WATER_SPEAR.get();
         boolean damaged = DamageSources.applyDamage(result.getEntity(), this.getDamage(), spell.getDamageSource(this, (Entity)(owner != null ? owner : this)));
         if (damaged && result.getEntity() instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(ModMobEffects.WET, 100, 0, false, false, true));
         }
      }
   }

   public void checkDespawn() {
      if (this.level().getDifficulty() != Difficulty.PEACEFUL) {
         super.checkDespawn();
      }
   }
}
