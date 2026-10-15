package net.alshanex.alshanex_familiars.entity.misc;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.Optional;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class PurpurPilarEntity extends AbstractMagicProjectile {
   public PurpurPilarEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public PurpurPilarEntity(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends Projectile>)EntityRegistry.PURPUR_PILAR_ENTITY.get(), pLevel);
      this.setOwner(owner);
   }

   public void trailParticles() {
      for (int i = 0; i < 1; i++) {
         Vec3 random = new Vec3(Utils.getRandomScaled(this.getBbWidth() * 0.5F), 0.0, Utils.getRandomScaled(this.getBbWidth() * 0.5F));
         this.level().addParticle(ParticleHelper.UNSTABLE_ENDER, this.getX() + random.x, this.getY() + 0.5, this.getZ() + random.z, 0.0, -0.05, 0.0);
      }
   }

   public void impactParticles(double x, double y, double z) {
   }

   protected void onHitEntity(EntityHitResult pResult) {
      super.onHitEntity(pResult);
      Entity targetEntity = pResult.getEntity();
      double knockbackStrength = 1.3;
      Vec3 knockback = new Vec3(-Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), 0.0, Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
         .normalize()
         .scale(knockbackStrength);
      targetEntity.push(-knockback.x, 0.1, -knockback.z);
      DamageSources.applyDamage(targetEntity, this.getDamage(), PetSpellRegistry.MAYHEM.get().getDamageSource(this, this.getOwner()));
      this.discard();
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   public void tick() {
      super.tick();
   }

   public float getSpeed() {
      return 1.0F;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean canBeHitByProjectile() {
      return true;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.STONE_HIT));
   }
}
