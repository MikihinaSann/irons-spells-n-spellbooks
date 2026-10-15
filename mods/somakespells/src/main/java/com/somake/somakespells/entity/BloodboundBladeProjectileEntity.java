package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModMobEffects;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.BloodboundBladeSpell;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class BloodboundBladeProjectileEntity extends AbstractMagicProjectile {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(
      BloodboundBladeProjectileEntity.class, EntityDataSerializers.ITEM_STACK
   );
   private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(BloodboundBladeProjectileEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> ROOT_DURATION = SynchedEntityData.defineId(BloodboundBladeProjectileEntity.class, EntityDataSerializers.INT);
   private static final DustParticleOptions CRIMSON_DUST = new DustParticleOptions(new Vector3f(0.72F, 0.02F, 0.05F), 0.8F);

   public BloodboundBladeProjectileEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public BloodboundBladeProjectileEntity(Level level, LivingEntity owner, ItemStack sword, float damage, int rootDuration) {
      this((EntityType<? extends Projectile>)ModEntityTypes.BLOODBOUND_BLADE_PROJECTILE.get(), level);
      this.setOwner(owner);
      this.setThrownItem(sword.copyWithCount(1));
      this.setDamage(damage);
      this.setRootDuration(rootDuration);
      this.setScale(owner.getScale());
      this.setPos(owner.getX(), owner.getEyeY() - 0.2, owner.getZ());
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ITEM, ItemStack.EMPTY);
      builder.define(DATA_SCALE, 1.0F);
      builder.define(ROOT_DURATION, 40);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.tickCount > 100) {
         this.discard();
      }
   }

   protected boolean canHitEntity(@NotNull Entity target) {
      return target instanceof LivingEntity && super.canHitEntity(target);
   }

   protected void onHitEntity(@NotNull EntityHitResult result) {
      super.onHitEntity(result);
      if (!this.level().isClientSide && !this.isRemoved()) {
         Entity owner = this.getOwner();
         if (result.getEntity() instanceof LivingEntity target && owner != target && (owner == null || !DamageSources.isFriendlyFireBetween(owner, target))) {
            SpellDamageSource source = ((BloodboundBladeSpell)ModSpells.BLOODBOUND_BLADE.get()).getDamageSource(this, (Entity)(owner != null ? owner : this));
            boolean damaged = DamageSources.applyDamage(target, this.getDamage(), source);
            if (damaged) {
               target.setDeltaMovement(Vec3.ZERO);
               boolean rooted = target.addEffect(new MobEffectInstance(ModMobEffects.CRIMSON_ROOT, this.getRootDuration(), 0, false, true, true));
               if (this.level() instanceof ServerLevel serverLevel) {
                  if (!this.getThrownItem().isEmpty()) {
                     EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, target, source, this.getThrownItem());
                  }

                  if (rooted || target.hasEffect(ModMobEffects.CRIMSON_ROOT)) {
                     BloodCrystalRootVisualEntity.attach(serverLevel, target);
                  }
               }
            }

            this.discard();
         } else {
            this.discard();
         }
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      if (!this.level().isClientSide) {
         this.discard();
      }
   }

   public void trailParticles() {
      Vec3 motion = this.getDeltaMovement();
      this.level()
         .addParticle(
            ParticleHelper.BLOOD,
            this.getX() - motion.x * 0.18,
            this.getY() - motion.y * 0.18,
            this.getZ() - motion.z * 0.18,
            -motion.x * 0.025,
            -motion.y * 0.025,
            -motion.z * 0.025
         );
      if ((this.tickCount & 1) == 0) {
         this.level().addParticle(CRIMSON_DUST, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void impactParticles(double x, double y, double z) {
      if (this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleHelper.BLOOD, x, y, z, 30, 0.32, 0.4, 0.32, 0.1);
         serverLevel.sendParticles(CRIMSON_DUST, x, y, z, 20, 0.25, 0.34, 0.25, 0.04);
         serverLevel.sendParticles(ParticleTypes.CRIT, x, y, z, 10, 0.2, 0.25, 0.2, 0.2);
         serverLevel.sendParticles(new BlastwaveParticleOptions(0.72F, 0.02F, 0.06F, 1.35F), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
      }
   }

   public float getSpeed() {
      return 1.75F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_CLUSTER_BREAK));
   }

   @Nullable
   public ItemStack getWeaponItem() {
      return this.getThrownItem();
   }

   public ItemStack getThrownItem() {
      return (ItemStack)this.entityData.get(DATA_ITEM);
   }

   private void setThrownItem(ItemStack stack) {
      this.entityData.set(DATA_ITEM, stack);
   }

   public float getScale() {
      return (Float)this.entityData.get(DATA_SCALE);
   }

   private void setScale(float scale) {
      this.entityData.set(DATA_SCALE, scale);
   }

   private int getRootDuration() {
      return (Integer)this.entityData.get(ROOT_DURATION);
   }

   private void setRootDuration(int duration) {
      this.entityData.set(ROOT_DURATION, Math.max(1, duration));
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      ItemStack item = this.getThrownItem();
      if (!item.isEmpty()) {
         tag.put("ThrownItem", item.save(this.level().registryAccess()));
      }

      tag.putFloat("RenderScale", this.getScale());
      tag.putInt("RootDuration", this.getRootDuration());
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.contains("ThrownItem")) {
         this.setThrownItem(ItemStack.parseOptional(this.level().registryAccess(), tag.getCompound("ThrownItem")));
      }

      this.setScale(tag.getFloat("RenderScale"));
      this.setRootDuration(tag.getInt("RootDuration"));
   }
}
