package com.somake.somakespells.spells.lightning;

import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.particle.ZapParticleOption;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
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

public class LightningStrikeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_strike");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(15.0)
      .build();

   public LightningStrikeSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 30;
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
      return Optional.of((SoundEvent)SoundRegistry.SHOCKWAVE_CAST.get());
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float range = 25.0F;
      Vec3 start = caster.getEyePosition();
      Vec3 forward = caster.getLookAngle();
      Vec3 end = start.add(forward.scale(range));
      HitResult hitResult = Utils.raycastForEntity(level, caster, start, end, true, 0.5F, e -> e != caster && e instanceof LivingEntity && !e.isSpectator());
      Vec3 visualStart = caster.getEyePosition().subtract(0.0, 0.2, 0.0);
      Vec3 visualEnd = hitResult.getLocation();
      if (hitResult.getType() == Type.ENTITY && hitResult instanceof EntityHitResult entityHit) {
         Entity target = entityHit.getEntity();
         visualEnd = target.getBoundingBox().getCenter();
         if (target instanceof LivingEntity livingTarget) {
            DamageSources.applyDamage(livingTarget, this.getDamage(spellLevel, caster), this.getDamageSource(caster));
            livingTarget.addEffect(new MobEffectInstance(ModMobEffects.ELECTROCUTED, this.getDuration(spellLevel, caster), spellLevel - 1));
         }
      }

      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(new ZapParticleOption(visualEnd), visualStart.x, visualStart.y, visualStart.z, 1, 0.0, 0.0, 0.0, 0.0);
         if (hitResult.getType() != Type.MISS) {
            serverLevel.sendParticles(ParticleHelper.ELECTRIC_SPARKS, visualEnd.x, visualEnd.y, visualEnd.z, 5, 0.1, 0.1, 0.1, 0.2);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 200 + spellLevel * 40;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
