package net.acetheeldritchking.discerning_the_eldritch.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.eldritch_caster.TheApostleEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.sightless_maw.SightlessMawEntity;
import net.acetheeldritchking.discerning_the_eldritch.entity.mobs.untold_behemoth.UntoldBehemothEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class ConjureForsakenAidSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "conjure_forsaken_aid");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(100.0)
      .build();

   public ConjureForsakenAidSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 30;
      this.baseManaCost = 100;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
         int summonTimer = (int)(20.0F * (20.0F * this.getSpellPower(spellLevel, entity)));

         for (int i = 0; i < spellLevel; i++) {
            Vec3 vec = entity.getEyePosition();
            double randomNearbyX = vec.x + entity.getRandom().nextGaussian() * 3.0;
            double randomNearbyZ = vec.z + entity.getRandom().nextGaussian() * 3.0;
            this.spawnForsakenAid(randomNearbyX, vec.y, randomNearbyZ, entity, level, summonTimer, spellLevel, summonedEntitiesCastData);
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTimer, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnForsakenAid(double x, double y, double z, LivingEntity caster, Level level, int timer, int spellLevel, SummonedEntitiesCastData castData) {
      boolean isMaw = Utils.random.nextDouble() < 0.65;
      boolean isApostle = Utils.random.nextDouble() < 0.25;
      boolean isBase = Utils.random.nextDouble() < 0.75;
      TheApostleEntity apostleEntity = new TheApostleEntity(level, caster);
      SightlessMawEntity sightlessMaw = new SightlessMawEntity(level, caster);
      UntoldBehemothEntity untoldBehemoth = new UntoldBehemothEntity(level, caster);
      AbstractSpellCastingMob isMeleeMobs = (AbstractSpellCastingMob)(isMaw ? sightlessMaw : untoldBehemoth);
      AbstractSpellCastingMob isCaster = (AbstractSpellCastingMob)(isApostle ? apostleEntity : isMeleeMobs);
      AbstractSpellCastingMob baseArmy = isBase ? isMeleeMobs : isCaster;
      baseArmy.setPos(x, y, z);
      baseArmy.setOldPosAndRot();
      AbstractSpellCastingMob event = (AbstractSpellCastingMob)((SpellSummonEvent)NeoForge.EVENT_BUS
            .post(new SpellSummonEvent(caster, baseArmy, this.spellId, spellLevel)))
         .getCreature();
      level.addFreshEntity(event);
      SummonManager.initSummon(caster, event, timer, castData);
   }
}
