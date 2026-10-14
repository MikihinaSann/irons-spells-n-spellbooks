package com.gametechbc.traveloptics.spells.nature;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.entity.spells.AerialCollapseVisualEntity;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOSounds;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.spells.TargetedTargetAreaCastData;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class AerialCollapseSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "aerial_collapse");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(45.0)
      .build();

   public AerialCollapseSpell() {
      super();
      this.manaCostPerLevel = 60;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 10;
      this.baseManaCost = 60;
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
      return Optional.of((SoundEvent)TOSounds.AERIAL_COLLAPSE.get());
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      if (!Utils.preCastTargetHelper(level, caster, playerMagicData, this, 32, 0.35F, true)) {
         return false;
      } else {
         LivingEntity target = ((TargetEntityCastData)playerMagicData.getAdditionalCastData()).getTarget((ServerLevel)level);
         float radius = this.getRadius(spellLevel, caster);
         TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, target.position(), radius, Utils.packRGB(this.getTargetingColor()), target);
         playerMagicData.setAdditionalCastData(new TargetedTargetAreaCastData(target, area));
         return true;
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
      caster.addEffect(new MobEffectInstance(TOEffects.AERIAL_COLLAPSE_HELPER, 3, 1, false, false, false));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         Entity targetEntity = targetData.getTarget((ServerLevel)level);
         if (targetEntity instanceof LivingEntity livingTarget) {
            float radius = this.getRadius(spellLevel, livingTarget);
            int effectLevel = (int)this.getDamage(spellLevel, caster);
            level.getEntitiesOfClass(
                  LivingEntity.class,
                  livingTarget.getBoundingBox().inflate(radius, 4.0, radius),
                  t -> !DamageSources.isFriendlyFireBetween(t, caster) && Utils.hasLineOfSight(level, livingTarget, t, true)
               )
               .forEach(t -> {
                  if (t.distanceToSqr(livingTarget) < radius * radius) {
                     t.addEffect(new MobEffectInstance(TOEffects.AERIAL_COLLAPSE, 55, effectLevel - 1));
                  }
               });
            if (!livingTarget.hasEffect(TOEffects.AERIAL_COLLAPSE)) {
               livingTarget.addEffect(new MobEffectInstance(TOEffects.AERIAL_COLLAPSE, 55, effectLevel - 1));
            }

            AerialCollapseVisualEntity aerialCollapseVisual = new AerialCollapseVisualEntity(level);
            aerialCollapseVisual.setOwner(caster);
            aerialCollapseVisual.setRadius(this.getRadius(spellLevel, caster));
            aerialCollapseVisual.setDuration(55);
            aerialCollapseVisual.setCircular();
            aerialCollapseVisual.setPos(targetEntity.position());
            level.addFreshEntity(aerialCollapseVisual);
            CylinderParticleManager.spawnParticles(
               level, livingTarget, 40 * spellLevel, ParticleHelper.ACID_BUBBLE, ParticleDirection.UPWARD, radius, 17.0, 0.0
            );
            MagicManager.spawnParticles(
               level,
               new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.NATURE.get()).getTargetingColor(), radius),
               livingTarget.getX(),
               livingTarget.getY() + 0.165F,
               livingTarget.getZ(),
               1,
               0.0,
               0.0,
               0.0,
               0.0,
               true
            );
         }
      }

      caster.addEffect(new MobEffectInstance(TOEffects.CASTING_EFFECT, 55, 2, false, false, false));
      caster.addEffect(new MobEffectInstance(TOEffects.AERIAL_COLLAPSE_HELPER, 55, 5, false, false, false));
      caster.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 70, 0, false, false, false));
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 2 + spellLevel * 2;
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return Math.min(12.5F + this.getSpellPower(spellLevel, caster) * 2.5F, 50.0F);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      int effectLevel = (int)this.getDamage(spellLevel, caster);
      return List.of(
         Component.translatable("ui.traveloptics.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)}),
         Component.translatable("ui.traveloptics.aerial_collapse_level.new", new Object[]{effectLevel}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public AnimationHolder getCastStartAnimation() {
      return TOSpellAnimations.AERIAL_COLLAPSE;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
