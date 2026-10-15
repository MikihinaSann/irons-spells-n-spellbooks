package com.snackpirate.aeromancy.spells.tornado;

import com.snackpirate.aeromancy.AASounds;
import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class TornadoSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(120.0)
      .build();

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("tornado");
   }

   public TornadoSpell() {
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 300;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      HitResult raycast = Utils.raycastForEntity(level, entity, 16.0F, true);
      Vec3 center = raycast.getLocation();
      TornadoEntity tornado = new TornadoEntity(level, entity);
      tornado.setSize(this.getSize(spellLevel, entity));
      tornado.moveTo(center);
      level.addFreshEntity(tornado);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getSize(int spellLevel, LivingEntity entity) {
      return 4 + 2 * spellLevel;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return java.util.Optional.of(AASounds.WIND_CAST_REVERSED.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.FINISH_ANIMATION;
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
