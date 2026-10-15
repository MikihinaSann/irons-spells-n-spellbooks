package com.somake.somakespells.entity;

import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModItems;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.blood.FragmentedRequiemSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FragmentedSwordRainEntity extends Projectile {
   private static final ResourceLocation BLOOD_CAST_SOUND_ID = ResourceLocation.parse("irons_spellbooks:cast.generic.blood");
   private static final ResourceLocation BLOOD_EXPLOSION_SOUND_ID = ResourceLocation.parse("irons_spellbooks:spell.sacrifice.blood_explosion");
   private static final EntityDataAccessor<Float> DATA_DAMAGE = SynchedEntityData.defineId(FragmentedSwordRainEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_STAGE = SynchedEntityData.defineId(FragmentedSwordRainEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> DATA_SWORD_Y_OFFSET = SynchedEntityData.defineId(FragmentedSwordRainEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> DATA_SWORD_PITCH = SynchedEntityData.defineId(FragmentedSwordRainEntity.class, EntityDataSerializers.FLOAT);
   private static final int STAGE_MARKING = 0;
   private static final int STAGE_RISING = 1;
   private static final int STAGE_HOLDING = 2;
   private static final int STAGE_FALLING = 3;
   private static final int MARK_DURATION = 12;
   private static final int RISE_DURATION = 8;
   private static final int HOLD_DURATION = 12;
   private static final int FALL_DURATION = 5;
   private static final float HIT_RADIUS = 1.1F;
   private static final float MAX_RISE_HEIGHT = 3.75F;
   private static final float INITIAL_SPAWN_OFFSET = -1.15F;
   private final Set<Integer> riseHitEntities = new HashSet<>();
   private final Set<Integer> fallHitEntities = new HashSet<>();
   private int stageAge;
   private float groundY;

   public FragmentedSwordRainEntity(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
      this.blocksBuilding = true;
   }

   public FragmentedSwordRainEntity(Level level, LivingEntity owner, Vec3 center, float damage) {
      this((EntityType<? extends Projectile>)ModEntityTypes.FRAGMENTED_SWORD_RAIN.get(), level);
      this.setOwner(owner);
      this.setPos(center.x, center.y, center.z);
      this.groundY = (float)center.y;
      this.entityData.set(DATA_DAMAGE, damage);
      this.entityData.set(DATA_STAGE, 0);
      this.entityData.set(DATA_SWORD_Y_OFFSET, -1.15F);
      this.entityData.set(DATA_SWORD_PITCH, 0.0F);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DATA_DAMAGE, 0.0F);
      builder.define(DATA_STAGE, 0);
      builder.define(DATA_SWORD_Y_OFFSET, -1.15F);
      builder.define(DATA_SWORD_PITCH, 0.0F);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.updateClientMotion();
      } else {
         LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
         if (owner != null && owner.isAlive()) {
            this.updateServerStage(owner);
         } else {
            this.discard();
         }
      }
   }

   private void updateServerStage(LivingEntity owner) {
      int stage = (Integer)this.entityData.get(DATA_STAGE);
      this.stageAge++;
      if (stage == 0) {
         this.entityData.set(DATA_SWORD_Y_OFFSET, -1.15F);
         this.entityData.set(DATA_SWORD_PITCH, 0.0F);
         if (this.stageAge >= 12) {
            this.playRiseSound();
            this.stageAge = 0;
            this.entityData.set(DATA_STAGE, 1);
         }
      } else if (stage == 1) {
         float progress = Math.min(1.0F, this.stageAge / 8.0F);
         this.entityData.set(DATA_SWORD_Y_OFFSET, lerp(-1.15F, 3.75F, progress));
         this.entityData.set(DATA_SWORD_PITCH, 0.0F);
         if (this.stageAge == 1) {
            this.applyDamage(owner, this.riseHitEntities);
         }

         if (this.stageAge >= 8) {
            this.stageAge = 0;
            this.entityData.set(DATA_STAGE, 2);
         }
      } else if (stage == 2) {
         this.entityData.set(DATA_SWORD_Y_OFFSET, 3.75F);
         float progressx = Math.min(1.0F, this.stageAge / 12.0F);
         this.entityData.set(DATA_SWORD_PITCH, lerp(0.0F, 180.0F, progressx));
         if (this.stageAge >= 12) {
            this.stageAge = 0;
            this.entityData.set(DATA_STAGE, 3);
         }
      } else {
         if (stage == 3) {
            float progressx = Math.min(1.0F, this.stageAge / 5.0F);
            this.entityData.set(DATA_SWORD_Y_OFFSET, lerp(3.75F, 0.0F, progressx));
            this.entityData.set(DATA_SWORD_PITCH, 180.0F);
            if (this.stageAge >= 5) {
               this.applyDamage(owner, this.fallHitEntities);
               this.spawnImpactBlood();
               this.discard();
            }
         }
      }
   }

   private void updateClientMotion() {
      int stage = (Integer)this.entityData.get(DATA_STAGE);
      if (stage == 0 || stage == 1 || stage == 2 || stage == 3) {
         ;
      }
   }

   private void spawnImpactBlood() {
      if (this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new BlastwaveParticleOptions(0.82F, 0.12F, 0.16F, 1.35F), this.getX(), this.groundY + 0.05, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0
         );
         MagicManager.spawnParticles(serverLevel, ParticleHelper.BLOOD, this.getX(), this.groundY + 0.12, this.getZ(), 22, 0.18, 0.08, 0.18, 0.05, true);
         MagicManager.spawnParticles(serverLevel, ParticleHelper.BLOOD, this.getX(), this.groundY + 0.18, this.getZ(), 32, 0.15, 0.35, 0.15, 0.18, true);
         SoundEvent var3 = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(BLOOD_EXPLOSION_SOUND_ID);
         serverLevel.playSound(null, this.getX(), this.groundY, this.getZ(), var3, SoundSource.PLAYERS, 1.15F, 1.0F);
      }
   }

   private void playRiseSound() {
      if (this.level() instanceof ServerLevel serverLevel) {
         SoundEvent bloodCast = (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(BLOOD_CAST_SOUND_ID);
         serverLevel.playSound(null, this.getX(), this.groundY, this.getZ(), bloodCast, SoundSource.PLAYERS, 1.0F, 1.0F);
      }
   }

   private void applyDamage(LivingEntity owner, Set<Integer> hitSet) {
      float damage = (Float)this.entityData.get(DATA_DAMAGE);
      if (!(damage <= 0.0F)) {
         AABB hitBox = new AABB(this.getX() - 1.1F, this.groundY, this.getZ() - 1.1F, this.getX() + 1.1F, this.groundY + 2.75F, this.getZ() + 1.1F);

         for (LivingEntity target : this.level()
            .getEntitiesOfClass(LivingEntity.class, hitBox, entity -> entity.isAlive() && entity != owner && !entity.isAlliedTo(owner))) {
            if (hitSet.add(target.getId())) {
               target.hurt(((FragmentedRequiemSpell)ModSpells.FRAGMENTED_REQUIEM.get()).getDamageSource(this, (Entity)(owner != null ? owner : this)), damage);
            }
         }
      }
   }

   public ItemStack getRenderStack() {
      return new ItemStack((ItemLike)ModItems.FRAGMENTED_SWORD_3.get());
   }

   public ItemDisplayContext getDisplayContext() {
      return ItemDisplayContext.FIXED;
   }

   public float getSwordYOffset() {
      return (Float)this.entityData.get(DATA_SWORD_Y_OFFSET);
   }

   public float getSwordPitch() {
      return (Float)this.entityData.get(DATA_SWORD_PITCH);
   }

   public boolean shouldRenderSword() {
      return (Integer)this.entityData.get(DATA_STAGE) != 0;
   }

   public static int totalLifetime() {
      return 37;
   }

   public float getGroundY() {
      return this.groundY;
   }

   public void setGroundY(float groundY) {
      this.groundY = groundY;
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      this.entityData.set(DATA_DAMAGE, compound.getFloat("Damage"));
      this.entityData.set(DATA_STAGE, compound.getInt("Stage"));
      this.entityData.set(DATA_SWORD_Y_OFFSET, compound.getFloat("SwordYOffset"));
      this.entityData.set(DATA_SWORD_PITCH, compound.getFloat("SwordPitch"));
      this.groundY = compound.getFloat("GroundY");
      this.stageAge = compound.getInt("StageAge");
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putFloat("Damage", (Float)this.entityData.get(DATA_DAMAGE));
      compound.putInt("Stage", (Integer)this.entityData.get(DATA_STAGE));
      compound.putFloat("SwordYOffset", (Float)this.entityData.get(DATA_SWORD_Y_OFFSET));
      compound.putFloat("SwordPitch", (Float)this.entityData.get(DATA_SWORD_PITCH));
      compound.putFloat("GroundY", this.groundY);
      compound.putInt("StageAge", this.stageAge);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return super.getAddEntityPacket(serverEntity);
   }

   public static Vec3 findSpawnPoint(ServerLevel level, Vec3 desiredCenter) {
      MutableBlockPos mutable = BlockPos.containing(desiredCenter).mutable();

      for (int down = 0; down < 6; down++) {
         BlockPos check = mutable.below(down);
         BlockState state = level.getBlockState(check);
         if (state.isFaceSturdy(level, check, Direction.UP)) {
            return new Vec3(desiredCenter.x, check.getY() + 1.02, desiredCenter.z);
         }
      }

      return desiredCenter;
   }

   private static float lerp(float start, float end, float progress) {
      return start + (end - start) * progress;
   }
}
