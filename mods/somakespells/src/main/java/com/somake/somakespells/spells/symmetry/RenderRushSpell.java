package com.somake.somakespells.spells.symmetry;

import com.github.L_Ender.cataclysm.client.particle.Options.RingParticleOptions;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.registries.ModMobEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RenderRushSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "render_rush");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.symmetrySchoolOrIce();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(5)
      .setCooldownSeconds(8.0)
      .build();

   public RenderRushSpell() {
      this.manaCostPerLevel = 5;
      this.baseManaCost = 20;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
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
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_RIPTIDE_1.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of();
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      float power = 2.5F + spellLevel * 0.2F;
      Vec3 forward = caster.getLookAngle();
      forward = new Vec3(forward.x, 0.0, forward.z).normalize();
      Vec3 vec = forward.scale(power);
      if (caster.onGround()) {
         caster.setPos(caster.position().add(0.0, 0.1, 0.0));
      }

      caster.setDeltaMovement(vec);
      caster.hurtMarked = true;
      caster.resetFallDistance();
      caster.hasImpulse = true;
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new RingParticleOptions(0.0F, (float) (Math.PI / 2), 20, 86, 236, 204, 1.0F, 20.0F, false, 0),
            caster.getX(),
            caster.getY() + 1.0,
            caster.getZ(),
            1,
            0.0,
            0.0,
            0.0,
            0.0
         );
      }

      caster.addEffect(new MobEffectInstance(ModMobEffects.RENDER_RUSH, 8, spellLevel, false, false, false));
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }
}
