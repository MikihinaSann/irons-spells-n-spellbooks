package net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_strike;

import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class EsotericStrike extends AoeEntity {
   private static final EntityDataAccessor<Boolean> DATA_IS_MIRRORED = SynchedEntityData.defineId(EsotericStrike.class, EntityDataSerializers.BOOLEAN);
   protected int effectAmplifier;
   protected float effectDuration;
   LivingEntity target;
   public final int ticksPerFrame = 2;
   public final int deathTime = 8;

   public EsotericStrike(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public EsotericStrike(Level level, boolean mirrored) {
      this((EntityType<? extends Projectile>)DTEEntityRegistry.ESOTERIC_STRIKE.get(), level);
      if (mirrored) {
         this.getEntityData().set(DATA_IS_MIRRORED, true);
      }
   }

   public void tick() {
      if (!this.firstTick) {
         this.checkHits();
         this.firstTick = true;
      }

      if (this.tickCount >= 8) {
         this.discard();
      }
   }

   public void applyEffect(LivingEntity target) {
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(DATA_IS_MIRRORED, false);
   }

   public boolean isMirrored() {
      return (Boolean)this.getEntityData().get(DATA_IS_MIRRORED);
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public float getParticleCount() {
      return 0.0F;
   }

   public Optional<ParticleOptions> getParticle() {
      return Optional.empty();
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }
}
