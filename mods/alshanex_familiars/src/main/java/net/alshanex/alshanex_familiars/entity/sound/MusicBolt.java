package net.alshanex.alshanex_familiars.entity.sound;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.guiding_bolt.GuidingBoltProjectile;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import java.util.Optional;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.alshanex_familiars.registry.ParticleRegistry;
import net.alshanex.alshanex_familiars.registry.PetSpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class MusicBolt extends GuidingBoltProjectile {
   public MusicBolt(EntityType<? extends GuidingBoltProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public MusicBolt(EntityType<? extends GuidingBoltProjectile> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.setOwner(shooter);
   }

   public MusicBolt(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends GuidingBoltProjectile>)EntityRegistry.MUSIC_BOLT.get(), levelIn, shooter);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, (ParticleOptions)ParticleRegistry.BLACK_NOTE_PARTICLE.get(), x, y, z, 25, 0.0, 0.0, 0.0, 0.18, true);
   }

   public float getSpeed() {
      return 1.3F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(AFSoundRegistry.CLEF);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 2.0F, 0.9F + Utils.random.nextFloat() * 0.4F);
   }

   protected void onHitBlock(BlockHitResult blockHitResult) {
      super.onHitBlock(blockHitResult);
      this.discard();
   }

   protected void onHitEntity(EntityHitResult entityHitResult) {
      super.onHitEntity(entityHitResult);
      if (DamageSources.applyDamage(entityHitResult.getEntity(), this.damage, PetSpellRegistry.MUSIC_BOLT.get().getDamageSource(this, this.getOwner()))
         && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.GUIDING_BOLT, 500));
      }

      this.discard();
   }

   public void trailParticles() {
   }
}
