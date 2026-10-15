package net.acetheeldritchking.discerning_the_eldritch.entity.spells.glacial_edge;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.ice_tomb.IceTombEntity;
import io.redspace.ironsspellbooks.entity.spells.snowball.FrostField;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GlacialEdge extends AbstractMagicProjectile implements AntiMagicSusceptible {
   private int lifetimeInTicks = 200;

   public GlacialEdge(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public GlacialEdge(EntityType<? extends Projectile> entityType, Level level, LivingEntity shooter) {
      this(entityType, level);
      this.setOwner(shooter);
      this.setYRot(shooter.getYRot());
      this.setXRot(shooter.getXRot());
   }

   public GlacialEdge(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)DTEEntityRegistry.GLACIAL_EDGE.get(), level, shooter);
   }

   public void travel() {
      this.setPos(this.position().add(this.getDeltaMovement()));
      if (!this.isNoGravity()) {
         Vec3 vec3 = this.getDeltaMovement();
         this.setDeltaMovement(vec3.x, vec3.y - 0.05F, vec3.z);
      }
   }

   public void tick() {
      this.lifetimeInTicks--;
      if (this.lifetimeInTicks <= 0) {
         this.discard();
      }

      Vec3 deltaMovement = this.getDeltaMovement();
      double distance = deltaMovement.horizontalDistance();
      double x = deltaMovement.x;
      double y = deltaMovement.y;
      double z = deltaMovement.z;
      this.setYRot((float)(Mth.atan2(x, z) * (180.0 / Math.PI)));
      this.setXRot((float)(Mth.atan2(y, distance) * (180.0 / Math.PI)));
      this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
      this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
      super.tick();
   }

   public void trailParticles() {
      for (int i = 0; i < 3; i++) {
         double speed = 0.05F;
         double dx = Math.random() * 2.0 * speed - speed;
         double dy = Math.random() * 2.0 * speed - speed;
         double dz = Math.random() * 2.0 * speed - speed;
         double radius = 4.0;
         Vec3 leftAdjust = this.position()
            .add(new Vec3(Math.sin(Math.toRadians(this.getYRot() + 90.0F)), 0.0, Math.cos(Math.toRadians(this.getYRot() + 90.0F))).scale(radius));
         Vec3 rightAdjust = this.position()
            .add(new Vec3(Math.sin(Math.toRadians(this.getYRot() - 90.0F)), 0.0, Math.cos(Math.toRadians(this.getYRot() - 90.0F))).scale(radius));
         this.level()
            .addParticle(
               (ParticleOptions)(Utils.random.nextDouble() < 0.3 ? ParticleHelper.SNOWFLAKE : ParticleTypes.SNOWFLAKE),
               this.getX() + dx,
               this.getY() + dy,
               this.getZ() + dz,
               dx,
               dy,
               dz
            );
         this.level()
            .addParticle(
               (ParticleOptions)(Utils.random.nextDouble() < 0.3 ? ParticleHelper.SNOWFLAKE : ParticleTypes.SNOWFLAKE),
               leftAdjust.x,
               leftAdjust.y,
               leftAdjust.z,
               dx,
               dy,
               dz
            );
         this.level()
            .addParticle(
               (ParticleOptions)(Utils.random.nextDouble() < 0.3 ? ParticleHelper.SNOWFLAKE : ParticleTypes.SNOWFLAKE),
               rightAdjust.x,
               rightAdjust.y,
               rightAdjust.z,
               dx,
               dy,
               dz
            );
      }
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), ParticleHelper.ICY_FOG, x, y, z, 1, 0.0, 0.0, 0.0, 0.1, true);
   }

   public float getSpeed() {
      return 1.0F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(SoundRegistry.ICE_IMPACT);
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   protected void onHitEntity(EntityHitResult pResult) {
      super.onHitEntity(pResult);
      DamageSources.applyDamage(pResult.getEntity(), this.getDamage(), SpellRegistries.GLACIAL_EDGE.get().getDamageSource(this, this.getOwner()));
      if (pResult.getEntity() instanceof LivingEntity livingTarget) {
         livingTarget.addEffect(new MobEffectInstance(MobEffectRegistry.CHILLED, 100, 1));
      }
   }

   private void createFrostField(Vec3 location) {
      if (!this.level().isClientSide) {
         FrostField frostField = new FrostField(this.level());
         frostField.setOwner(this.getOwner());
         frostField.setDuration(100);
         frostField.setRadius(6.0F);
         frostField.setCircular();
         frostField.moveTo(location);
         this.level().addFreshEntity(frostField);
      }
   }

   private void entombEntity(Entity entity) {
      if (!entity.isPassenger()) {
         IceTombEntity iceTombEntity = new IceTombEntity(this.level(), this.getOwner());
         iceTombEntity.moveTo(entity.position());
         iceTombEntity.setDeltaMovement(entity.getDeltaMovement());
         iceTombEntity.setLifetime(100);
         iceTombEntity.setEvil();
         this.level().addFreshEntity(iceTombEntity);
         entity.startRiding(iceTombEntity, true);
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
      this.discard();
   }

   protected void onHit(HitResult hitresult) {
      super.onHit(hitresult);
      this.createFrostField(Utils.moveToRelativeGroundLevel(this.level(), hitresult.getLocation(), 2));
   }

   protected boolean canHitEntity(Entity pTarget) {
      return pTarget != this.getOwner() && super.canHitEntity(pTarget);
   }

   public void onAntiMagic(MagicData playerMagicData) {
      MagicManager.spawnParticles(this.level(), ParticleHelper.ICY_FOG, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.1, true);
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }
}
