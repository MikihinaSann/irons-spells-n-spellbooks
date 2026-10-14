package com.gametechbc.traveloptics.spells.ender;

import com.gametechbc.traveloptics.init.TOEffects;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

@AutoSpellConfig
public class AstralSenseSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "astral_sense");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(120.0)
      .build();

   public AstralSenseSpell() {
      super();
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 100;
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

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SLASH_ANIMATION;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int duration = this.getDuration(spellLevel, caster);
      int amplifier = 0;
      AABB searchBox = caster.getBoundingBox().inflate(this.getRadius(spellLevel));

      for (Mob mob : level.getEntitiesOfClass(Mob.class, searchBox, Entity::isAlive)) {
         mob.addEffect(new MobEffectInstance(TOEffects.ASTRAL_PATH_REVEAL, duration, amplifier, false, false, false));
         mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, amplifier, false, false, false));
      }

      if (caster.isCrouching()) {
         caster.addEffect(new MobEffectInstance(TOEffects.ASTRAL_SENSE_TREASURE, duration, amplifier, false, false, false));
      } else {
         caster.addEffect(new MobEffectInstance(TOEffects.ASTRAL_SENSE, duration, amplifier, false, false, false));
      }

      ScreenShake_Entity.ScreenShake(level, caster.position(), 12.0F, 0.02F, 10, 20);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(800.0F + this.getSpellPower(spellLevel, caster) * 100.0F);
   }

   private double getRadius(int spellLevel) {
      return 20 + spellLevel * 5;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 2)}),
         Component.translatable("ui.traveloptics.duration", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }
}
