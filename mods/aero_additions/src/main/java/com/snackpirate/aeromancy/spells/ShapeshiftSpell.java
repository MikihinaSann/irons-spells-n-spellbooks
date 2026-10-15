package com.snackpirate.aeromancy.spells;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.network.AeromancySpellData;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class ShapeshiftSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(70.0)
      .build();

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("shapeshift");
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public ShapeshiftSpell() {
      this.manaCostPerLevel = 7;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 10;
      this.castTime = 30;
      this.baseManaCost = 60;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (Utils.preCastTargetHelper(level, entity, playerMagicData, this, 5, 0.15F)) {
         LivingEntity target = ((TargetEntityCastData)playerMagicData.getAdditionalCastData()).getTarget((ServerLevel)level);
         if (target == null) {
            return false;
         } else {
            playerMagicData.setAdditionalCastData(new TargetEntityCastData(target));
            return true;
         }
      } else {
         return false;
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData target
         && level instanceof ServerLevel server
         && target.getTarget(server) != null) {
         AeromancySpellData.getAeromancyData(entity).setShapeshiftId(BuiltInRegistries.ENTITY_TYPE.getKey(target.getTarget(server).getType()));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
