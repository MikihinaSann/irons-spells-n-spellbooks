package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.spirit.SoulReprisalSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ThrownPhantomDaggerEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class SoulReprisalDaggerEntity extends ThrownPhantomDaggerEntity {
   private static final EntityDataAccessor<Float> SPELL_DAMAGE = SynchedEntityData.defineId(SoulReprisalDaggerEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> LIFETIME_TICKS = SynchedEntityData.defineId(SoulReprisalDaggerEntity.class, EntityDataSerializers.INT);
   private final Set<UUID> hitEntities = new HashSet<>();

   public SoulReprisalDaggerEntity(EntityType<? extends ThrownPhantomDaggerEntity> type, Level level) {
      super(type, level);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SPELL_DAMAGE, 0.0F);
      builder.define(LIFETIME_TICKS, 112);
   }

   public void setSpellDamage(float damage) {
      this.entityData.set(SPELL_DAMAGE, damage);
   }

   public void setLifetimeTicks(int lifetimeTicks) {
      this.entityData.set(LIFETIME_TICKS, lifetimeTicks);
   }

   public int getLessLifeTicks() {
      return Math.round(100.0F + this.getReturnTick() - ((Integer)this.entityData.get(LIFETIME_TICKS)).intValue());
   }

   protected float getDamage() {
      return (Float)this.entityData.get(SPELL_DAMAGE);
   }

   protected void onHitEntity(EntityHitResult result) {
      Entity target = result.getEntity();
      Entity owner = this.getOwner();
      if (target instanceof LivingEntity living && target != owner && (owner == null || !living.isAlliedTo(owner)) && this.hitEntities.add(target.getUUID())) {
         DamageSources.applyDamage(
            target, this.getDamage(), ((SoulReprisalSpell)ModSpells.SOUL_REPRISAL.get()).getDamageSource(this, (Entity)(owner != null ? owner : this))
         );
      }
   }
}
