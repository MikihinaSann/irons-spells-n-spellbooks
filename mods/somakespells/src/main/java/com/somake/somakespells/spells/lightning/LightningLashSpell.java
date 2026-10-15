package com.somake.somakespells.spells.lightning;

import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class LightningLashSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_lash");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(8.0)
      .build();

   public LightningLashSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 25;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.SHOCKWAVE_CAST.get());
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level world, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float range = 10.0F;
      Vec3 start = caster.getEyePosition();
      Vec3 forward = caster.getLookAngle();
      Vec3 end = start.add(forward.scale(range));
      HitResult hitResult = Utils.raycastForEntity(world, caster, start, end, true, 0.5F, e -> e != caster && e instanceof LivingEntity && !e.isSpectator());
      Vec3 forwardView = caster.getViewVector(1.0F);
      Vec3 right = forwardView.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      Vec3 visualStart = caster.getEyePosition().subtract(0.0, 0.2, 0.0).add(right.scale(0.3));
      Vec3 visualEnd = end;
      if (hitResult.getType() == Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
         Entity target = entityHit.getEntity();
         visualEnd = target.getBoundingBox().getCenter();
         boolean damaged = DamageSources.applyDamage(target, this.getDamage(spellLevel, caster), this.getDamageSource(caster));
         if (damaged && target instanceof LivingEntity livingTarget) {
            livingTarget.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, 100, 0, true, true, true));
         }

         Vec3 pull = caster.position().subtract(target.position()).normalize().scale(3.0).multiply(1.0, 0.25, 1.0);
         target.setDeltaMovement(target.getDeltaMovement().add(pull));
         target.hasImpulse = true;
         if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleHelper.ELECTRIC_SPARKS, visualEnd.x, visualEnd.y, visualEnd.z, 15, 0.2, 0.2, 0.2, 0.2);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, visualEnd.x, visualEnd.y, visualEnd.z, 10, 0.3, 0.3, 0.3, 0.1);
         }
      } else if (hitResult.getType() == Type.BLOCK) {
         visualEnd = hitResult.getLocation();
         if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleHelper.ELECTRIC_SPARKS, visualEnd.x, visualEnd.y, visualEnd.z, 5, 0.1, 0.1, 0.1, 0.1);
         }
      }

      if (world instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(new ZapParticleOption(visualEnd), visualStart.x, visualStart.y, visualStart.z, 1, 0.0, 0.0, 0.0, 0.0);
         int steps = 5;
         Vec3 delta = visualEnd.subtract(visualStart).scale(1.0 / steps);

         for (int i = 1; i < steps; i++) {
            Vec3 pos = visualStart.add(delta.scale(i));
            serverLevel.sendParticles(ParticleHelper.ELECTRICITY, pos.x, pos.y, pos.z, 1, 0.1, 0.1, 0.1, 0.0);
         }
      }

      super.onCast(world, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
