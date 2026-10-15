package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.aqua.SummonDrownedSpell;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericFollowOwnerGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtByTargetGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.GenericOwnerHurtTargetGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;

public class SummonedDrownedEntity extends Drowned implements IMagicSummon {
   public SummonedDrownedEntity(EntityType<? extends Drowned> entityType, Level level) {
      super(entityType, level);
   }

   public void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(3, new GenericFollowOwnerGoal(this, this::getSummoner, 1.1, 10.0F, 2.0F, false, 25.0F));
      this.targetSelector.addGoal(1, new GenericOwnerHurtByTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(2, new GenericOwnerHurtTargetGoal(this, this::getSummoner));
      this.targetSelector.addGoal(6, new HurtByTargetGoal(this, new Class[0]));
   }

   public LivingEntity getSummoner() {
      return (LivingEntity)SummonManager.getOwner(this);
   }

   public void onUnSummon() {
      this.discard();
   }

   public boolean isAlliedTo(Entity entity) {
      return this.isAlliedHelper(entity) || super.isAlliedTo(entity);
   }

   public boolean doHurtTarget(Entity target) {
      LivingEntity summoner = this.getSummoner();
      Entity attacker = (Entity)(summoner != null ? summoner : this);
      float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      return DamageSources.applyDamage(target, damage, ((SummonDrownedSpell)ModSpells.SUMMON_DROWNED.get()).getDamageSource(this, attacker));
   }

   public void die(DamageSource damageSource) {
      this.onDeathHelper();
      super.die(damageSource);
   }

   public void remove(RemovalReason reason) {
      super.remove(reason);
      this.onRemovedHelper(this);
   }

   public boolean shouldDropExperience() {
      return false;
   }

   public boolean shouldDropLoot() {
      return false;
   }
}
