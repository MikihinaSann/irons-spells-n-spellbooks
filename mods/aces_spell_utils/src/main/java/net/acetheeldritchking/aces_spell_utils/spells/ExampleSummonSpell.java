package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.mobs.SummonedSkeleton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ExampleSummonSpell extends AbstractSummonSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("aces_spell_utils", "example_summon_spells");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(200.0)
      .build();

   public ExampleSummonSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 80;
      this.baseManaCost = 200;
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

   @Override
   protected int onSummoningCast(
      Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData, SummonedEntitiesCastData castData
   ) {
      int summonTimer = 12000;

      for (int i = 0; i < spellLevel; i++) {
         Vec3 vec = caster.getEyePosition();
         double randomNearbyX = vec.x + caster.getRandom().nextGaussian() * 3.0;
         double randomNearbyZ = vec.z + caster.getRandom().nextGaussian() * 3.0;
         this.spawnHelper(
            randomNearbyX,
            vec.y,
            randomNearbyZ,
            caster,
            level,
            summonTimer,
            castData,
            () -> new SummonedSkeleton(level, caster, true),
            this.spellId,
            spellLevel
         );
      }

      return summonTimer;
   }
}
