package com.snackpirate.aeromancy.spells.wind_charge;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class WindChargeSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(1.0)
      .build();

   public WindChargeSpell() {
      this.manaCostPerLevel = 8;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 3;
      this.castTime = 0;
      this.baseManaCost = 20;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.strength", new Object[]{String.format("%s%%", (int)(this.getDamage(spellLevel, caster) * 100.0F))})
      );
   }

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("wind_charge");
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public SchoolType getSchoolType() {
      return AASpells.Schools.WIND.get();
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      MagicWindCharge charge = new MagicWindCharge(level, entity);
      charge.setPos(entity.position().add(0.0, entity.getEyeHeight() - charge.getBoundingBox().getYsize() * 0.5, 0.0));
      charge.setDamage(this.getDamage(spellLevel, entity));
      charge.shoot(entity.getLookAngle());
      level.addFreshEntity(charge);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return 0.2F * (float)Math.sqrt(this.getSpellPower(spellLevel, entity)) / 0.6324555F;
   }
}
