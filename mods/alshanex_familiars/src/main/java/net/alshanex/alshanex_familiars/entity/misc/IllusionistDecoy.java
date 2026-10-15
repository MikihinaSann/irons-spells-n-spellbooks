package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.entity.AbstractSpellCastingPet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent.Start;
import net.neoforged.neoforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IllusionistDecoy extends LivingEntity implements GeoEntity {
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;
   private int age;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public IllusionistDecoy(EntityType<? extends LivingEntity> entityType, Level level) {
      super(entityType, level);
   }

   public IllusionistDecoy(Level level, LivingEntity pet) {
      this((EntityType<? extends LivingEntity>)EntityRegistry.DECOY.get(), level);
      this.setOwner(pet);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.age % 10 == 0 && this.getOwner() != null) {
            List<Mob> mobs = this.level()
               .getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(10.0), entity -> entity != this.getOwner())
               .stream()
               .toList();
            if (!mobs.isEmpty()) {
               for (Mob mob : mobs) {
                  if (!(mob instanceof AbstractSpellCastingPet)) {
                     mob.setTarget(this);
                  }
               }
            }
         }

         if (this.age > 200) {
            this.discard();
         }
      }

      this.age++;
   }

   public boolean hurt(DamageSource pSource, float pAmount) {
      if (!this.level.isClientSide && this.getOwner() != null) {
         float explosionRadius = 4.0F;
         float explosionRadiusSqr = explosionRadius * explosionRadius;
         List<Entity> entities = this.level
            .getEntities(this, this.getBoundingBox().inflate(explosionRadius), entityx -> entityx != this.getOwner() && !(entityx instanceof IllusionistDecoy));
         Vec3 losPoint = Utils.raycastForBlock(this.level, this.position(), this.position().add(0.0, 2.0, 0.0), Fluid.NONE).getLocation();

         for (Entity entity : entities) {
            double distanceSqr = entity.distanceToSqr(this.position());
            if (distanceSqr < explosionRadiusSqr && Utils.hasLineOfSight(this.level, losPoint, entity.getBoundingBox().getCenter(), true)) {
               double p = 1.0 - distanceSqr / explosionRadiusSqr;
               float damage = (float)(10.0 * p);
               if (this.getOwner() != null) {
                  damage = (float)((this.getOwner().getMaxHealth() - this.getOwner().getHealth()) / 2.0F * p);
               }

               DamageSources.applyDamage(entity, damage, ((AbstractSpell)SpellRegistry.FIREBALL_SPELL.get()).getDamageSource(this, this.getOwner()));
            }
         }

         if ((Boolean)ServerConfigs.SPELL_GREIFING.get()) {
            Explosion explosion = new Explosion(
               this.level,
               null,
               ((AbstractSpell)SpellRegistry.FIREBALL_SPELL.get()).getDamageSource(this, this.getOwner()),
               null,
               this.getX(),
               this.getY(),
               this.getZ(),
               explosionRadius / 2.0F,
               true,
               BlockInteraction.DESTROY,
               ParticleTypes.EXPLOSION,
               ParticleTypes.EXPLOSION_EMITTER,
               SoundEvents.GENERIC_EXPLODE
            );
            if (!((Start)NeoForge.EVENT_BUS.post(new Start(this.level, explosion))).isCanceled()) {
               explosion.explode();
               explosion.finalizeExplosion(false);
            }
         }

         PacketDistributor.sendToPlayersTrackingEntity(
            this, new FieryExplosionParticlesPacket(new Vec3(this.getX(), this.getY() + 0.15F, this.getZ()), explosionRadius), new CustomPacketPayload[0]
         );
         this.playSound(
            (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F
         );
      }

      this.discard();
      return super.hurt(pSource, pAmount);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.age = pCompound.getInt("Age");
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }
   }

   public Iterable<ItemStack> getArmorSlots() {
      return Collections.singleton(ItemStack.EMPTY);
   }

   public ItemStack getItemBySlot(EquipmentSlot pSlot) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("Age", this.age);
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   public void setOwner(@Nullable LivingEntity pOwner) {
      this.owner = pOwner;
      this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
