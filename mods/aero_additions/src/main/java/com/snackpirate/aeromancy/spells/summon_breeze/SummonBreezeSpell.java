package com.snackpirate.aeromancy.spells.summon_breeze;

import com.snackpirate.aeromancy.Aeromancy;
import com.snackpirate.aeromancy.spells.AASpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

@AutoSpellConfig
public class SummonBreezeSpell extends AbstractSpell {
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(AASpells.Schools.WIND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public ResourceLocation getSpellResource() {
      return Aeromancy.id("summon_breeze");
   }

   public SummonBreezeSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 50;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return 2;
   }

   public void onRecastFinished(ServerPlayer serverPlayer, RecastInstance recastInstance, RecastResult recastResult, ICastDataSerializable castDataSerializable) {
      if (SummonManager.recastFinishedHelper(serverPlayer, recastInstance, recastResult, castDataSerializable)) {
         super.onRecastFinished(serverPlayer, recastInstance, recastResult, castDataSerializable);
      }
   }

   public ICastDataSerializable getEmptyCastData() {
      return new SummonedEntitiesCastData();
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
         int summonTime = 12000;
         SummonedBreeze breeze = new SummonedBreeze(world, entity);
         breeze.setPos(entity.position());
         SummonedBreeze creature = (SummonedBreeze)((SpellSummonEvent)NeoForge.EVENT_BUS
               .post(new SpellSummonEvent(entity, breeze, this.getSpellResource(), spellLevel)))
            .getCreature();
         world.addFreshEntity(creature);
         SummonManager.initSummon(entity, creature, summonTime, summonedEntitiesCastData);
         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }
}
