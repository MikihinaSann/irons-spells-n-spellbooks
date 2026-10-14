package com.gametechbc.traveloptics.spells.eldritch;

import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.spells.TOSpellAnimations;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class SpectralBlinkSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "spectral_blink");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(25.0)
      .build();

   public SpectralBlinkSpell() {
      super();
      this.baseManaCost = 10;
      this.manaCostPerLevel = 20;
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

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      int effectLevel = this.getEffectLevel(spellLevel, caster);
      return List.of(Component.translatable("ui.traveloptics.range", new Object[]{effectLevel}), Component.literal("§9T.O Magic 'n Extras"));
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int duration = 65;
      int effectLevel = this.getEffectLevel(spellLevel, entity);
      entity.addEffect(new MobEffectInstance(TOEffects.SPECTRAL_BLINK, duration, effectLevel - 1, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public int getEffectLevel(int spellLevel, LivingEntity caster) {
      return 10 + (int)(this.getSpellPower(spellLevel, caster) * 10.0F);
   }

   public AnimationHolder getCastFinishAnimation() {
      return TOSpellAnimations.SPECTRAL_BLINK;
   }
}
