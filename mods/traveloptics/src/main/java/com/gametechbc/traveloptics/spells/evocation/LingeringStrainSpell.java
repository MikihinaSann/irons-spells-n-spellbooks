package com.gametechbc.traveloptics.spells.evocation;

import com.gametechbc.traveloptics.init.TOEffects;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class LingeringStrainSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "lingering_strain");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(120.0)
      .build();

   public LingeringStrainSpell() {
      super();
      this.baseManaCost = 50;
      this.manaCostPerLevel = 50;
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
      return List.of(
         Component.translatable("ui.traveloptics.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(caster, spellLevel), 1)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int duration = this.getDuration(entity, spellLevel);
      entity.addEffect(new MobEffectInstance(TOEffects.LINGERING_STRAIN, duration, 0, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public int getDuration(LivingEntity caster, int spellLevel) {
      return 230 + (int)this.getSpellPower(spellLevel, caster) * 90;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
