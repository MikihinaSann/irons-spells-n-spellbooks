package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.WingedRuinSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarExplosionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class WingedRuinSoulPillarEntity extends SoulPillarExplosionEntity implements IronSpellDamageEntity {
   public WingedRuinSoulPillarEntity(
      Level level, double x, double y, double z, float rotation, int warmupDelay, LivingEntity owner, int lifeTicks, float damage, boolean red
   ) {
      super(level, x, y, z, rotation, warmupDelay, owner, lifeTicks, damage, red);
   }

   @Override
   public boolean somake$applySpellDamage(LivingEntity target, float amount) {
      LivingEntity owner = this.getCaster();
      DamageSources.applyDamage(target, amount, ((WingedRuinSpell)ModSpells.WINGED_RUIN.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)));
      return false;
   }
}
