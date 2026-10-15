package net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_edge;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.AbstractShieldEntity;
import io.redspace.ironsspellbooks.entity.spells.ShieldPart;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.acetheeldritchking.discerning_the_eldritch.particle.DTEParticleHelper;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEEntityRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class EsotericEdge extends AbstractMagicProjectile implements AntiMagicSusceptible {
   private final List<Entity> entities = new ArrayList<>();
   private int lifetimeInTicks = 300;

   public EsotericEdge(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public EsotericEdge(EntityType<? extends Projectile> entityType, Level level, LivingEntity shooter) {
      this(entityType, level);
      this.setOwner(shooter);
      this.setYRot(shooter.getYRot());
      this.setXRot(shooter.getXRot());
   }

   public EsotericEdge(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)DTEEntityRegistry.ESOTERIC_EDGE.get(), level, shooter);
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

      if (!this.level().isClientSide) {
         HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
         if (hitresult.getType() == Type.BLOCK) {
            this.onHitBlock((BlockHitResult)hitresult);
         }

         for (Entity entity : this.level()
            .getEntities(this, this.getBoundingBox())
            .stream()
            .filter(target -> this.canHitEntity(target) && !this.entities.contains(target))
            .collect(Collectors.toSet())) {
            this.damageEntity(entity);
         }
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
         this.level().addParticle(DTEParticleHelper.ESOTERIC_SPARKS, leftAdjust.x, leftAdjust.y, leftAdjust.z, dx, dy, dz);
         this.level().addParticle(DTEParticleHelper.ESOTERIC_SPARKS, rightAdjust.x, rightAdjust.y, rightAdjust.z, dx, dy, dz);
      }
   }

   public void impactParticles(double x, double y, double z) {
   }

   public float getSpeed() {
      return 1.2F;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   private void damageEntity(Entity entity) {
      if (!this.entities.contains(entity)) {
         DamageSources.applyDamage(entity, this.damage, SpellRegistries.ESOTERIC_EDGE.get().getDamageSource(this, this.getOwner()));
         if (entity instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
            if (livingTarget instanceof Player player) {
               player.disableShield();
            }

            if (DamageSources.applyDamage(livingTarget, this.damage, SpellRegistries.ESOTERIC_EDGE.get().getDamageSource(this, this.getOwner()))) {
               EnchantmentHelper.doPostAttackEffects(
                  (ServerLevel)this.level(), livingTarget, SpellRegistries.ESOTERIC_EDGE.get().getDamageSource(this, this.getOwner())
               );
            }
         }

         if (entity instanceof ShieldPart || entity instanceof AbstractShieldEntity) {
            entity.kill();
         }

         this.entities.add(entity);
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      super.onHitBlock(result);
   }

   protected boolean canHitEntity(Entity pTarget) {
      return pTarget != this.getOwner() && super.canHitEntity(pTarget);
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.empty();
   }

   public void onAntiMagic(MagicData playerMagicData) {
   }

   public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
      super.recreateFromPacket(pPacket);
      this.xRotO = this.getXRot();
      this.yRotO = this.getYRot();
   }
}
