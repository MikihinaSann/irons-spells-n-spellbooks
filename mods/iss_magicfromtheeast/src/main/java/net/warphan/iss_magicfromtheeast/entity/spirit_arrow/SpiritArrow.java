package net.warphan.iss_magicfromtheeast.entity.spirit_arrow;

import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.warphan.iss_magicfromtheeast.damage.MFTEDamageTypes;
import net.warphan.iss_magicfromtheeast.enchantment.MFTEEnchantmentHelper;
import net.warphan.iss_magicfromtheeast.registries.MFTEEffectRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTEEntityRegistries;

public class SpiritArrow extends AbstractMagicProjectile {
   private static final EntityDataAccessor<Boolean> SOUL_FLAME = SynchedEntityData.defineId(SpiritArrow.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> GHOSTLY_COLD = SynchedEntityData.defineId(SpiritArrow.class, EntityDataSerializers.BOOLEAN);
   private ItemStack firedFromWeapon;
   int onWorldTick = -1;

   public SpiritArrow(EntityType<? extends SpiritArrow> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
      this.firedFromWeapon = null;
   }

   public SpiritArrow(Level level, Entity shooter, @Nullable ItemStack stack) {
      this((EntityType<? extends SpiritArrow>)MFTEEntityRegistries.SPIRIT_ARROW.get(), level);
      this.setOwner(shooter);
      this.firedFromWeapon = stack;
      if (stack != null && level instanceof ServerLevel serverLevel) {
         MFTEEnchantmentHelper.onSpiritArrowSpawned(serverLevel, stack, this);
      }
   }

   public void shoot(Vec3 rotation) {
      this.setDeltaMovement(rotation);
   }

   public void trailParticles() {
      Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
      pos = pos.add(this.getDeltaMovement());
      this.level.addParticle(ParticleTypes.ENCHANTED_HIT, pos.x, pos.y - 0.25, pos.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 64.0F;
   }

   public void tick() {
      super.tick();
      this.onWorldTick--;
      if (this.onWorldTick == 0) {
         this.discard();
      }

      if ((Boolean)this.entityData.get(SOUL_FLAME)) {
         Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
         pos = pos.add(this.getDeltaMovement());
         this.level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y - 0.25, pos.z, 0.0, 0.0, 0.0);
      }

      if ((Boolean)this.entityData.get(GHOSTLY_COLD)) {
         Vec3 pos = this.getBoundingBox().getCenter().add(this.getDeltaMovement());
         pos = pos.add(this.getDeltaMovement());
         this.level.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y - 0.25, pos.z, 0.0, 0.0, 0.0);
      }
   }

   public void setOnWorldTick(int onWorldTick) {
      this.onWorldTick = onWorldTick;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public boolean canHitEntity(Entity pTarget) {
      return super.canHitEntity(pTarget);
   }

   public void handleHitDetection() {
      Vec3 vec3 = this.getDeltaMovement();
      Vec3 pos = this.position();
      Vec3 vec32 = pos.add(vec3);
      HitResult hitResult = this.level.clip(new ClipContext(pos, vec32, Block.COLLIDER, Fluid.NONE, this));
      if (hitResult.getType() != Type.MISS) {
         this.onHit(hitResult);
      } else {
         for (Entity entity : this.level.getEntities(this, this.getBoundingBox().inflate(0.25), this::canHitEntity)) {
            this.onHit(new EntityHitResult(entity, this.getBoundingBox().getCenter().add(entity.getBoundingBox().getCenter()).scale(0.5)));
         }
      }
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      Entity target = entityHitResult.getEntity();
      if (this.getOwner() != null) {
         if (target instanceof LivingEntity livingEntity) {
            DamageSources.ignoreNextKnockback(livingEntity);
            if (this.getSoulFlame()) {
               livingEntity.addEffect(new MobEffectInstance(MFTEEffectRegistries.SOULBURN, 100, 0));
            }

            if (this.getGhostlyCold()) {
               livingEntity.setTicksFrozen(500);
               if (!this.level.isClientSide) {
                  MagicManager.spawnParticles(
                     this.level, ParticleTypes.SNOWFLAKE, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 4, 0.6, 0.6, 0.6, 0.3, false
                  );
               }
            }
         }

         DamageSources.applyDamage(target, this.damage, this.damageSources().source(MFTEDamageTypes.SPIRIT_ARROW, this.getOwner()));
      }
   }

   public ItemStack getWeaponItem() {
      return this.firedFromWeapon;
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if (this.firedFromWeapon != null) {
         compoundTag.put("weapon", this.firedFromWeapon.save(this.registryAccess(), new CompoundTag()));
      }
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      if (compoundTag.contains("weapon", 10)) {
         this.firedFromWeapon = (ItemStack)ItemStack.parse(this.registryAccess(), compoundTag.getCompound("weapon")).orElse(null);
      } else {
         this.firedFromWeapon = null;
      }
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(SOUL_FLAME, false);
      pBuilder.define(GHOSTLY_COLD, false);
   }

   public boolean getSoulFlame() {
      return (Boolean)this.entityData.get(SOUL_FLAME);
   }

   public void setSoulFlame() {
      this.entityData.set(SOUL_FLAME, true);
   }

   public boolean getGhostlyCold() {
      return (Boolean)this.entityData.get(GHOSTLY_COLD);
   }

   public void setGhostlyCold() {
      this.entityData.set(GHOSTLY_COLD, true);
   }
}
