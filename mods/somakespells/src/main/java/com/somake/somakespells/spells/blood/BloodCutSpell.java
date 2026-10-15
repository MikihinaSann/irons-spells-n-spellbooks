package com.somake.somakespells.spells.blood;

import com.somake.somakespells.effect.blood.BloodCutEffect;
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
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BloodCutSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "blood_cut");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(60.0)
      .build();

   public BloodCutSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable(
            "ui.irons_spellbooks.percent_damage", new Object[]{Utils.stringTruncation(BloodCutEffect.getDamageModifier(spellLevel - 1, caster) * 100.0F, 0)}
         ),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)})
      );
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int duration = (int)(this.getSpellPower(spellLevel, entity) * 20.0F);
      entity.addEffect(new MobEffectInstance(ModMobEffects.BLOOD_CUT, duration, spellLevel - 1, false, false, true));
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setLifestealPercent(0.15F);
   }
}
