package net.acetheeldritchking.cataclysm_spellbooks.entity.spells.hellish_blade;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModEffect;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.blazing_aoe.BlazingAoE;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSEntityRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.SpellRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HellishBladeProjectile extends AbstractMagicProjectile implements GeoEntity {
   private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
   private static final EntityDataAccessor<Boolean> SOUL = SynchedEntityData.defineId(HellishBladeProjectile.class, EntityDataSerializers.BOOLEAN);

   public HellishBladeProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setNoGravity(true);
   }

   public HellishBladeProjectile(Level level, LivingEntity shooter) {
      this((EntityType<? extends Projectile>)CSEntityRegistry.HELLISH_BLADE_PROJECTILE.get(), level);
      this.setOwner(shooter);
   }

   public void trailParticles() {
      Vec3 vec3 = this.position().subtract(this.getDeltaMovement());
      this.level().addParticle((ParticleOptions)ModParticle.TRAP_FLAME.get(), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
   }

   public void impactParticles(double x, double y, double z) {
      MagicManager.spawnParticles(this.level(), (ParticleOptions)ModParticle.TRAP_FLAME.get(), x, y, z, 5, 0.0, 0.0, 0.0, 1.0, true);
   }

   public float getSpeed() {
      return 0.8F;
   }

   public Optional<Holder<SoundEvent>> getImpactSound() {
      return Optional.of(ModSounds.IGNIS_IMPACT);
   }

   protected void doImpactSound(Holder<SoundEvent> sound) {
      this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.5F, 1.0F);
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity target = pResult.getEntity();
      DamageSources.applyDamage(target, this.damage, SpellRegistries.HELLISH_BLADE.get().getDamageSource(this, this.getOwner()));
      if (target instanceof LivingEntity livingTarget) {
         livingTarget.addEffect(new MobEffectInstance(ModEffect.EFFECTBLAZING_BRAND, 100, 0));
         livingTarget.addEffect(new MobEffectInstance(ModEffect.EFFECTSTUN, 60, 0));
         if (livingTarget instanceof Player playerTarget) {
            playerTarget.disableShield();
         }

         ScreenShake_Entity.ScreenShake(this.level(), livingTarget.position(), 20.0F, 0.1F, 20, 40);
      }

      this.discard();
   }

   protected void onHit(HitResult hitresult) {
      super.onHit(hitresult);
      this.createAoEField(hitresult.getLocation());
      EarthquakeAoe aoe = new EarthquakeAoe(this.level());
      aoe.moveTo(this.position());
      aoe.setOwner(this);
      aoe.setCircular();
      aoe.setRadius(10.0F);
      aoe.setDuration(20);
      aoe.setDamage(1.0F);
      aoe.setSlownessAmplifier(0);
      this.level().addFreshEntity(aoe);
      this.discard();
   }

   public void createAoEField(Vec3 location) {
      if (!this.level().isClientSide) {
         BlazingAoE aoE = new BlazingAoE(this.level());
         aoE.setOwner(this.getOwner());
         aoE.setDuration(100);
         aoE.setDamage(1.5F);
         aoE.setRadius(4.5F);
         aoE.setCircular();
         aoE.moveTo(location);
         this.level().addFreshEntity(aoE);
      }
   }

   public boolean getIsSoul() {
      return (Boolean)this.entityData.get(SOUL);
   }

   public void setIsSoul(boolean soul) {
      this.entityData.set(SOUL, soul);
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }

   protected void defineSynchedData(Builder pBuilder) {
      super.defineSynchedData(pBuilder);
      pBuilder.define(SOUL, false);
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setIsSoul(pCompound.getBoolean("Soul"));
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("Soul", this.getIsSoul());
   }
}
