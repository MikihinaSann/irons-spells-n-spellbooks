package com.gametechbc.traveloptics.entity.extended;

import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.init.TOSpells;
import com.github.L_Ender.cataclysm.blocks.EMP_Block;
import com.github.L_Ender.cataclysm.client.particle.Options.LightningParticleOptions;
import com.github.L_Ender.cataclysm.config.CMCommonConfig;
import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModBlocks;
import com.github.L_Ender.cataclysm.init.ModTag;
import com.github.L_Ender.cataclysm.util.CMDamageTypes;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.event.EventHooks;

public class ExtendedDeathLaserBeamEntity extends Death_Laser_Beam_Entity {
   public ExtendedDeathLaserBeamEntity(EntityType<? extends Death_Laser_Beam_Entity> type, Level world) {
      super(type, world);
   }

   public ExtendedDeathLaserBeamEntity(
      EntityType<? extends Death_Laser_Beam_Entity> type,
      Level world,
      LivingEntity caster,
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      int duration,
      float damage,
      float hpDamage
   ) {
      super(type, world, caster, x, y, z, yaw, pitch, duration, damage, hpDamage);
   }

   public void tick() {
      this.prevCollidePosX = this.collidePosX;
      this.prevCollidePosY = this.collidePosY;
      this.prevCollidePosZ = this.collidePosZ;
      this.prevYaw = this.renderYaw;
      this.prevPitch = this.renderPitch;
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
      if (this.tickCount == 1 && this.level().isClientSide) {
         this.caster = (LivingEntity)this.level().getEntity(this.getCasterID());
      }

      if (!this.level().isClientSide && this.caster != null) {
         this.updateWithLivingEntity();
      }

      if (this.caster != null) {
         this.renderYaw = (float)((this.caster.yHeadRot + 90.0) * Math.PI / 180.0);
         this.renderPitch = (float)(-this.caster.getXRot() * Math.PI / 180.0);
      }

      if (!this.on && this.appear.getTimer() == 0) {
         this.discard();
      }

      if (this.on && this.tickCount > 20) {
         this.appear.increaseTimer();
      } else {
         this.appear.decreaseTimer();
      }

      if (this.caster != null && !this.caster.isAlive()) {
         this.discard();
      }

      if (this.tickCount > 20) {
         this.updatedCalculateEndPos();
         List<LivingEntity> hit = this.updatedRaytraceEntities(
               this.level(), new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(this.endPosX, this.endPosY, this.endPosZ), false, true, true
            )
            .entities;
         if (this.blockSide != null) {
            this.spawnUpdatedExplosionParticles(5);
            if (!this.level().isClientSide) {
               if ((Boolean)SpellsConfig.deathLaserBlockBreaking.get()) {
                  for (BlockPos pos : BlockPos.betweenClosed(
                     Mth.floor(this.collidePosX - 0.5),
                     Mth.floor(this.collidePosY - 0.5),
                     Mth.floor(this.collidePosZ - 0.5),
                     Mth.floor(this.collidePosX + 0.5),
                     Mth.floor(this.collidePosY + 0.5),
                     Mth.floor(this.collidePosZ + 0.5)
                  )) {
                     BlockState block = this.level().getBlockState(pos);
                     if (!block.isAir() && block.is(ModTag.CM_GLASS) && EventHooks.canEntityGrief(this.level(), this)) {
                        this.level().destroyBlock(pos, true);
                     }
                  }
               }

               for (BlockPos posx : BlockPos.betweenClosed(
                  Mth.floor(this.collidePosX - 2.5),
                  Mth.floor(this.collidePosY - 2.5),
                  Mth.floor(this.collidePosZ - 2.5),
                  Mth.floor(this.collidePosX + 2.5),
                  Mth.floor(this.collidePosY + 2.5),
                  Mth.floor(this.collidePosZ + 2.5)
               )) {
                  BlockState block = this.level().getBlockState(posx);
                  if (block.is((Block)ModBlocks.EMP.get()) && !(Boolean)block.getValue(EMP_Block.POWERED) && (Boolean)block.getValue(EMP_Block.OVERLOAD)) {
                     this.level().setBlockAndUpdate(posx, (BlockState)block.setValue(EMP_Block.OVERLOAD, false));
                  }
               }

               if (this.getFire()) {
                  BlockPos blockpos1 = BlockPos.containing(this.collidePosX, this.collidePosY, this.collidePosZ);
                  if (CMCommonConfig.Harbinger.HarbingerLightFire) {
                     if (this.level().isEmptyBlock(blockpos1)) {
                        this.level().setBlockAndUpdate(blockpos1, BaseFireBlock.getState(this.level(), blockpos1));
                     }
                  } else if (EventHooks.canEntityGrief(this.level(), this) && this.level().isEmptyBlock(blockpos1)) {
                     this.level().setBlockAndUpdate(blockpos1, BaseFireBlock.getState(this.level(), blockpos1));
                  }
               }
            }
         }

         if (!this.level().isClientSide) {
            for (LivingEntity target : hit) {
               if ((Boolean)SpellsConfig.deathLaserDealMagicDamage.get()) {
                  if (this.caster != null && !this.caster.isAlliedTo(target) && target != this.caster) {
                     boolean flag = target.hurt(
                        CMDamageTypes.causeDeathLaserDamage(this, this.caster),
                        (float)(this.getDamage() + Math.min((double)this.getDamage(), target.getMaxHealth() * this.getHpDamage() * 0.01))
                     );
                     if (this.getFire() && flag) {
                        target.igniteForSeconds(5.0F);
                     }
                  }
               } else if (this.caster != null && !this.caster.isAlliedTo(target) && target != this.caster) {
                  boolean flag = DamageSources.applyDamage(
                     target,
                     (float)(this.getDamage() + Math.min((double)this.getDamage(), target.getMaxHealth() * this.getHpDamage() * 0.01)),
                     TOSpells.DEATH_LASER_SPELL.get().getDamageSource(this, this.caster)
                  );
                  if (this.getFire() && flag) {
                     target.igniteForSeconds(5.0F);
                  }
               }
            }
         }
      }

      if (this.tickCount - 20 > this.getDuration()) {
         this.on = false;
      }
   }

   protected void spawnUpdatedExplosionParticles(int amount) {
      for (int i = 0; i < amount; i++) {
         float velocity = 1.5F;
         float yaw = (float)(this.random.nextFloat() * 2.0F * Math.PI);
         float motionY = this.random.nextFloat() * 0.8F;
         float motionX = 1.5F * Mth.cos(yaw);
         float motionZ = 1.5F * Mth.sin(yaw);
         this.level()
            .addParticle(new LightningParticleOptions(255, 26, 0), this.collidePosX, this.collidePosY + 0.1, this.collidePosZ, motionX, motionY, motionZ);
      }
   }

   private void updateWithLivingEntity() {
      this.setYaw((float)((this.caster.yHeadRot + 90.0F) * Math.PI / 180.0));
      this.setPitch((float)(-this.caster.getXRot() * Math.PI / 180.0));
      double eyeOffset = 0.06;
      this.setPos(this.caster.getX(), this.caster.getY() + this.caster.getEyeHeight() - eyeOffset, this.caster.getZ());
   }

   private void updatedCalculateEndPos() {
      if (this.level().isClientSide()) {
         this.endPosX = this.getX() + 30.0 * Math.cos(this.renderYaw) * Math.cos(this.renderPitch);
         this.endPosZ = this.getZ() + 30.0 * Math.sin(this.renderYaw) * Math.cos(this.renderPitch);
         this.endPosY = this.getY() + 30.0 * Math.sin(this.renderPitch);
      } else {
         this.endPosX = this.getX() + 30.0 * Math.cos(this.getYaw()) * Math.cos(this.getPitch());
         this.endPosZ = this.getZ() + 30.0 * Math.sin(this.getYaw()) * Math.cos(this.getPitch());
         this.endPosY = this.getY() + 30.0 * Math.sin(this.getPitch());
      }
   }

   public ExtendedDeathLaserBeamEntity.DeathLaserbeamHitResult updatedRaytraceEntities(
      Level world, Vec3 from, Vec3 to, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock
   ) {
      ExtendedDeathLaserBeamEntity.DeathLaserbeamHitResult result = new ExtendedDeathLaserBeamEntity.DeathLaserbeamHitResult();
      result.setBlockHit(world.clip(new ClipContext(from, to, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, this)));
      if (result.blockHit != null) {
         Vec3 hitVec = result.blockHit.getLocation();
         this.collidePosX = hitVec.x;
         this.collidePosY = hitVec.y;
         this.collidePosZ = hitVec.z;
         this.blockSide = result.blockHit.getDirection();
      } else {
         this.collidePosX = this.endPosX;
         this.collidePosY = this.endPosY;
         this.collidePosZ = this.endPosZ;
         this.blockSide = null;
      }

      for (LivingEntity entity : world.getEntitiesOfClass(
         LivingEntity.class,
         new AABB(
               Math.min(this.getX(), this.collidePosX),
               Math.min(this.getY(), this.collidePosY),
               Math.min(this.getZ(), this.collidePosZ),
               Math.max(this.getX(), this.collidePosX),
               Math.max(this.getY(), this.collidePosY),
               Math.max(this.getZ(), this.collidePosZ)
            )
            .inflate(1.0, 1.0, 1.0)
      )) {
         if (entity != this.caster) {
            float pad = entity.getPickRadius() + 0.5F;
            AABB aabb = entity.getBoundingBox().inflate(pad, pad, pad);
            Optional<Vec3> hit = aabb.clip(from, to);
            if (aabb.contains(from)) {
               result.addEntityHit(entity);
            } else if (hit.isPresent()) {
               result.addEntityHit(entity);
            }
         }
      }

      return result;
   }

   public static class DeathLaserbeamHitResult {
      private BlockHitResult blockHit;
      private final List<LivingEntity> entities = new ArrayList<>();

      public DeathLaserbeamHitResult() {
         super();
      }

      public BlockHitResult getBlockHit() {
         return this.blockHit;
      }

      public void setBlockHit(HitResult rayTraceResult) {
         if (rayTraceResult.getType() == Type.BLOCK) {
            this.blockHit = (BlockHitResult)rayTraceResult;
         }
      }

      public void addEntityHit(LivingEntity entity) {
         this.entities.add(entity);
      }
   }
}
