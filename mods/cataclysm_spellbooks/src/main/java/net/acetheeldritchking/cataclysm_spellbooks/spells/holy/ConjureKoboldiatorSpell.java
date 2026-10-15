package net.acetheeldritchking.cataclysm_spellbooks.spells.holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import java.util.List;
import javax.annotation.Nullable;
import net.acetheeldritchking.cataclysm_spellbooks.entity.mobs.SummonedKoboldiator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

public class ConjureKoboldiatorSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "conjure_koboldiator");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(200.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.cataclysm_spellbooks.koboldiator_count", new Object[]{spellLevel}));
   }

   public ConjureKoboldiatorSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 80;
      this.baseManaCost = 250;
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
            this.spawnKoboldiator(randomNearbyX, vec.y, randomNearbyZ, entity, level, summonTimer, spellLevel, summonedEntitiesCastData);
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTimer, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnKoboldiator(
      double x, double y, double z, LivingEntity caster, Level level, int summonTimer, int spellLevel, SummonedEntitiesCastData castData
   ) {
      SummonedKoboldiator kobolediator = new SummonedKoboldiator(level, caster);
      SummonedKoboldiator event = (SummonedKoboldiator)((SpellSummonEvent)NeoForge.EVENT_BUS
            .post(new SpellSummonEvent(caster, kobolediator, this.spellId, spellLevel)))
         .getCreature();
      kobolediator.moveTo(x, y, z);
      kobolediator.setSleep(false);
      level.addFreshEntity(event);
      SummonManager.initSummon(caster, event, summonTimer, castData);
   }
}
