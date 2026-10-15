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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.ChorusFruit;

public class ChorusFlowerEntity extends AbstractMagicProjectile {
   public ChorusFlowerEntity(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public ChorusFlowerEntity(Level pLevel, LivingEntity owner) {
      this((EntityType<? extends Projectile>)EntityRegistry.CHORUS_FLOWER_ENTITY.get(), pLevel);
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
      if (targetEntity instanceof LivingEntity pEntityLiving && !this.level().isClientSide) {
         DamageSources.applyDamage(pEntityLiving, this.getDamage(), PetSpellRegistry.MAYHEM.get().getDamageSource(this, this.getOwner()));
         targetEntity.invulnerableTime = 0;

         for (int i = 0; i < 16; i++) {
            double d0 = pEntityLiving.getX() + (pEntityLiving.getRandom().nextDouble() - 0.5) * 16.0;
            double d1 = Mth.clamp(
               pEntityLiving.getY() + (pEntityLiving.getRandom().nextInt(16) - 8),
               this.level().getMinBuildHeight(),
               this.level().getMinBuildHeight() + ((ServerLevel)this.level()).getLogicalHeight() - 1
            );
            double d2 = pEntityLiving.getZ() + (pEntityLiving.getRandom().nextDouble() - 0.5) * 16.0;
            if (pEntityLiving.isPassenger()) {
               pEntityLiving.stopRiding();
            }

            Vec3 vec3 = pEntityLiving.position();
            ChorusFruit event = EventHooks.onChorusFruitTeleport(pEntityLiving, d0, d1, d2);
            if (pEntityLiving.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
               this.level().gameEvent(GameEvent.TELEPORT, vec3, Context.of(pEntityLiving));
               pEntityLiving.resetFallDistance();
               break;
            }
         }

         if (pEntityLiving instanceof Player player) {
            player.resetCurrentImpulseContext();
         }
      }

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
      return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.CHORUS_FRUIT_TELEPORT));
   }
}
