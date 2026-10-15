package net.alshanex.alshanex_familiars.entity.sound;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlackNoteEntity extends AbstractMagicProjectile implements GeoEntity {
   private final RawAnimation ANIMATION_LOOP = RawAnimation.begin().thenLoop("idle");
   private final AnimationController controller = new AnimationController(this, "note_controller", 0, this::animationPredicate);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BlackNoteEntity(EntityType<? extends AbstractMagicProjectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public BlackNoteEntity(EntityType<? extends AbstractMagicProjectile> entityType, Level levelIn, LivingEntity shooter) {
      this(entityType, levelIn);
      this.setOwner(shooter);
   }

   public BlackNoteEntity(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends AbstractMagicProjectile>)EntityRegistry.BLACK_NOTE.get(), levelIn, shooter);
   }

   public void trailParticles() {
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level, (ParticleOptions)ParticleRegistry.BLACK_NOTE_PARTICLE.get(), x, y, z, 25, 0.0, 0.0, 0.0, 0.18, true);
   }

   public float getSpeed() {
      return 0.5F;
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
      if (DamageSources.applyDamage(
            entityHitResult.getEntity(), this.damage * 1.1F, PetSpellRegistry.DEFAULT_NOTE_SPELL.get().getDamageSource(this, this.getOwner())
         )
         && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
         livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.GUIDING_BOLT, 500));
      }

      this.discard();
   }

   public void setRotation(float x, float y) {
      this.setXRot(x);
      this.xRotO = x;
      this.setYRot(y);
      this.yRotO = y;
   }

   private PlayState animationPredicate(AnimationState event) {
      AnimationController controller = event.getController();
      controller.setAnimation(this.ANIMATION_LOOP);
      return PlayState.CONTINUE;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(this.controller);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
