package net.acetheeldritchking.cataclysm_spellbooks.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
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
import java.util.List;
import javax.annotation.Nullable;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedAptrgangr;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedEliteDraugur;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedRoyalDraugur;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

public class ConjureThrallsSpell extends AbstractMaledictusSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "conjure_thralls");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(120.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.cataclysm_spellbooks.thrall_count", new Object[]{spellLevel}));
   }

   public ConjureThrallsSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 80;
      this.baseManaCost = 50;
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
         int summonTimer = 12000;

         for (int i = 0; i < spellLevel; i++) {
            Vec3 vec = entity.getEyePosition();
            double randomNearbyX = vec.x + entity.getRandom().nextGaussian() * 3.0;
            double randomNearbyZ = vec.z + entity.getRandom().nextGaussian() * 3.0;
            this.spawnThrallsNearby(randomNearbyX, vec.y, randomNearbyZ, entity, level, summonTimer, spellLevel, summonedEntitiesCastData);
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTimer, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnThrallsNearby(
      double x, double y, double z, LivingEntity caster, Level level, int summonTimer, int spellLevel, SummonedEntitiesCastData castData
   ) {
      boolean isRoyal = Utils.random.nextDouble() < 0.4;
      boolean isElite = Utils.random.nextDouble() < 0.5;
      boolean isAptrgangr = Utils.random.nextDouble() < 0.2;
      boolean isBase = Utils.random.nextDouble() < 0.6;
      boolean isFullArmy = Utils.random.nextDouble() < 0.75;
      SummonedDraugur draugur = new SummonedDraugur(level, caster);
      SummonedRoyalDraugur royalDraugur = new SummonedRoyalDraugur(level, caster);
      SummonedEliteDraugur eliteDraugur = new SummonedEliteDraugur(level, caster);
      SummonedAptrgangr aptrgangr = new SummonedAptrgangr(level, caster);
      Monster isBaseDraugur = (Monster)(isRoyal ? draugur : royalDraugur);
      Monster isEliteDraugur = (Monster)(isElite ? eliteDraugur : royalDraugur);
      Monster isAptrgangrDraugur = (Monster)(isAptrgangr ? aptrgangr : isEliteDraugur);
      Monster baseArmy = isBase ? isBaseDraugur : isEliteDraugur;
      Monster draugurArmry = isFullArmy ? baseArmy : isAptrgangrDraugur;
      Monster event = (Monster)((SpellSummonEvent)NeoForge.EVENT_BUS.post(new SpellSummonEvent(caster, draugurArmry, this.spellId, spellLevel))).getCreature();
      draugurArmry.finalizeSpawn((ServerLevelAccessor)level, level.getCurrentDifficultyAt(draugurArmry.getOnPos()), MobSpawnType.MOB_SUMMONED, null);
      draugurArmry.moveTo(x, y, z);
      level.addFreshEntity(event);
      SummonManager.initSummon(caster, event, summonTimer, castData);
   }
}
