package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SoulfallJudgmentSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.AnimatedEntity.FallingSoulBladeEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulfallJudgmentBladeEntity extends FallingSoulBladeEntity implements IronSpellDamageEntity {
   private final Set<UUID> hitTargets = new HashSet<>();

   public SoulfallJudgmentBladeEntity(Level level, double x, double y, double z, float rotation, int warmupDelay, LivingEntity owner, float damage, boolean red) {
      super(level, x, y, z, rotation, warmupDelay, owner, damage, red);
   }

   @Override
   public boolean somake$applySpellDamage(LivingEntity target, float ignoredAmount) {
      if (!this.level().isClientSide && this.hitTargets.add(target.getUUID())) {
         LivingEntity owner = this.getCaster();
         DamageSources.applyDamage(
            target, this.getDamage(), ((SoulfallJudgmentSpell)ModSpells.SOULFALL_JUDGMENT.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
         );
         return false;
      } else {
         return false;
      }
   }

   public boolean shouldBeSaved() {
      return false;
   }
}
