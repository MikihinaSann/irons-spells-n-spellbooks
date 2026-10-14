package com.gametechbc.traveloptics.spells.ender;

import com.gametechbc.traveloptics.api.particle.CircleParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.entity.effect.Void_Vortex_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class VortexPunchSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "vortex_punch");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(18.0)
      .build();

   public VortexPunchSpell() {
      super();
      this.manaCostPerLevel = 70;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 15;
      this.baseManaCost = 70;
   }

   public CastType getCastType() {
      return CastType.LONG;
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
      return Optional.of((SoundEvent)SoundRegistry.ELDRITCH_BLAST.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.VORTEX_PUNCH_CHARGE;
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.VORTEX_PUNCH;
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      CircleParticleManager.spawnParticles(level, entity, 30, ParticleHelper.UNSTABLE_ENDER, ParticleDirection.INWARD, 3.0);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, caster, playerMagicData, this, 20, 0.2F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         Entity targetEntity = targetData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            Void_Vortex_Entity vortexEntity = new Void_Vortex_Entity(
               level, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), caster.getYRot(), caster, this.getDuration() + 10
            );
            vortexEntity.setOwner(caster);
            level.addFreshEntity(vortexEntity);
            double pullRadius = 3.5;

            for (Entity nearbyEntity : level.getEntities(
               targetEntity, targetEntity.getBoundingBox().inflate(pullRadius), entity -> entity instanceof LivingEntity && entity != targetEntity
            )) {
               Vec3 direction = targetEntity.position().subtract(nearbyEntity.position()).normalize().scale(1.0);
               nearbyEntity.setDeltaMovement(nearbyEntity.getDeltaMovement().add(direction));
            }

            CircleParticleManager.spawnParticles(level, caster, 50, ParticleTypes.CAMPFIRE_COSY_SMOKE, ParticleDirection.OUTWARD, 3.0);
            ScreenShake_Entity.ScreenShake(level, caster.position(), 15.0F, 0.02F, 10, 20);
            int duration = this.getDuration();
            caster.addEffect(new MobEffectInstance(TOEffects.VORTEX_PUNCH, duration, this.getDamage(spellLevel, caster), false, false, false));
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.range", new Object[]{Utils.stringTruncation(20.0, 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public int getDuration() {
      return 25;
   }

   private int getDamage(int spellLevel, LivingEntity caster) {
      return (int)(10.0F + this.getSpellPower(spellLevel, caster) * 10.0F);
   }
}
