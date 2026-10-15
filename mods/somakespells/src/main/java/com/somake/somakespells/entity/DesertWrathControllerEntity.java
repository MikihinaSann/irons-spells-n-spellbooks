package com.somake.somakespells.entity;

import com.github.L_Ender.cataclysm.entity.projectile.Cursed_Sandstorm_Entity;
import com.somake.somakespells.registries.ModSpells;
import com.somake.somakespells.spells.symmetry.DesertWrathSpell;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DesertWrathControllerEntity extends Entity {
   private LivingEntity owner;
   private float damage;
   private int duration;
   private int amount;
   private List<Cursed_Sandstorm_Entity> sandstorms = new ArrayList<>();
   private float angle = 0.0F;

   public DesertWrathControllerEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   public DesertWrathControllerEntity(EntityType<?> type, Level level, LivingEntity owner, float damage, int duration, int amount) {
      this(type, level);
      this.owner = owner;
      this.damage = damage;
      this.duration = duration;
      this.amount = amount;
      this.setPos(owner.getX(), owner.getY(), owner.getZ());
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         if (this.owner != null && this.owner.isAlive()) {
            if (this.tickCount >= this.duration) {
               this.discardSandstorms();
               this.discard();
            } else {
               if (this.tickCount == 1) {
                  for (int i = 0; i < this.amount; i++) {
                     Cursed_Sandstorm_Entity sandstorm = new Cursed_Sandstorm_Entity(this.level(), this.owner);
                     sandstorm.setDamage(this.damage);
                     sandstorm.setUp(10);
                     this.level().addFreshEntity(sandstorm);
                     this.sandstorms.add(sandstorm);
                  }
               }

               this.angle += 0.1F;
               float radius = 3.0F;

               for (int i = 0; i < this.sandstorms.size(); i++) {
                  Cursed_Sandstorm_Entity sandstorm = this.sandstorms.get(i);
                  if (sandstorm.isAlive()) {
                     float offset = (float)((Math.PI * 2) * i / this.amount);
                     float currentAngle = this.angle + offset;
                     double x = this.owner.getX() + Math.cos(currentAngle) * radius;
                     double z = this.owner.getZ() + Math.sin(currentAngle) * radius;
                     double y = this.owner.getY();
                     sandstorm.setPos(x, y, z);
                     sandstorm.setDeltaMovement(Vec3.ZERO);
                     sandstorm.setPower(Vec3.ZERO);
                     if (sandstorm.tickCount % 5 == 0 && !this.level().isClientSide) {
                        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, sandstorm.getBoundingBox().inflate(0.5))) {
                           if (target != this.owner && !target.isAlliedTo(this.owner)) {
                              target.hurt(((DesertWrathSpell)ModSpells.DESERT_WRATH.get()).getDamageSource(sandstorm, this.owner), this.damage);
                              sandstorm.discard();
                              break;
                           }
                        }
                     }
                  }
               }

               this.setPos(this.owner.position());
            }
         } else {
            this.discardSandstorms();
            this.discard();
         }
      }
   }

   private void discardSandstorms() {
      for (Cursed_Sandstorm_Entity s : this.sandstorms) {
         if (s.isAlive()) {
            s.discard();
         }
      }

      this.sandstorms.clear();
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }
}
