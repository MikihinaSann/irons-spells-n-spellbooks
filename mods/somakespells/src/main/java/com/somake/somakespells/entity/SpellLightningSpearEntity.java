package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.effect.Lightning_Area_Effect_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Lightning_Storm_Entity;
import com.github.L_Ender.cataclysm.entity.projectile.Lightning_Spear_Entity;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpellLightningSpearEntity extends Lightning_Spear_Entity {
   private boolean shouldSpawnCloud = true;
   private AbstractSpell damageSpell;

   public SpellLightningSpearEntity(EntityType<? extends Lightning_Spear_Entity> type, Level level) {
      super(type, level);
   }

   public SpellLightningSpearEntity(
      EntityType<? extends Lightning_Spear_Entity> type, LivingEntity caster, double x, double y, double z, Vec3 dir, float damage, Level level
   ) {
      super(type, caster, x, y, z, dir, damage, level);
   }

   public void setSpawnCloud(boolean spawn) {
      this.shouldSpawnCloud = spawn;
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
         AbstractSpell spell = this.damageSpell != null ? this.damageSpell : (AbstractSpell)ModSpells.LIGHTNING_SPEAR.get();
         boolean damaged = DamageSources.applyDamage(result.getEntity(), this.getDamage(), spell.getDamageSource(this, (Entity)(owner != null ? owner : this)));
         if (damaged && result.getEntity() instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 100, 0, true, true, true));
         }

         if (this.shouldSpawnCloud) {
            Entity target = result.getEntity();
            this.spawnLightningCloud(target.getX(), target.getY(), target.getZ());
         }

         this.discard();
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      if (!this.level().isClientSide) {
         if (this.shouldSpawnCloud) {
            BlockPos pos = result.getBlockPos();
            double spawnY = result.getLocation().y;
            if (result.getDirection() == Direction.UP) {
               spawnY = pos.getY() + 1.0;
            }

            this.spawnLightningCloud(result.getLocation().x, spawnY, result.getLocation().z);
         }

         this.discard();
      }
   }

   public void checkDespawn() {
      if (this.level().getDifficulty() != Difficulty.PEACEFUL) {
         super.checkDespawn();
      }
   }

   private void spawnLightningCloud(double x, double y, double z) {
      if (this.level().getEntitiesOfClass(Lightning_Area_Effect_Entity.class, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5)).isEmpty()) {
         LivingEntity owner = this.getOwner() instanceof LivingEntity livingOwner ? livingOwner : null;
         if (this.getAreaDamage() > 0.0F && this.getAreaRadius() > 0.0F) {
            LightningFieldEntity field = new LightningFieldEntity(this.level(), owner, this.getAreaRadius(), 60, this.getAreaDamage());
            field.setDamageSpell(this.damageSpell != null ? this.damageSpell : (AbstractSpell)ModSpells.LIGHTNING_SPEAR.get());
            field.setPos(x, y, z);
            this.level().addFreshEntity(field);
         }
      }
   }
}
