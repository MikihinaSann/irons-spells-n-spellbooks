package net.warphan.iss_magicfromtheeast.spells.spirit;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.warphan.iss_magicfromtheeast.entity.mobs.spirit_ashigaru.SpiritAshigaruEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

@AutoSpellConfig
public class AshigaruSquadSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "ashigaru_squad");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(MFTESchoolRegistries.SPIRIT_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(180.0)
      .build();

   public AshigaruSquadSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 3;
      this.castTime = 20;
      this.baseManaCost = 60;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{this.getAshigaruHealth(spellLevel, null)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getAshigaruDamage(spellLevel, null)}),
         Component.translatable("ui.iss_magicfromtheeast.armor", new Object[]{this.getAshigaruArmor(spellLevel, null)}),
         Component.translatable("ui.irons_spellbooks.summon_count", new Object[]{spellLevel})
      );
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAISE_DEAD_START.value());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.SPIRIT_INVOKING.value());
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
      int summonTime = 12000;
      float radius = 1.87F;
      PlayerRecasts recast = playerMagicData.getPlayerRecasts();
      if (!recast.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();

         for (int i = 0; i < spellLevel; i++) {
            SpiritAshigaruEntity ashigaru = new SpiritAshigaruEntity(world, entity, true);
            if ((i + 1) % 3 == 0) {
               ashigaru.setRangeType();
               ashigaru.getAttributes().getInstance(Attributes.ENTITY_INTERACTION_RANGE).setBaseValue(25.0);
            } else {
               ashigaru.setMeleeType();
            }

            ashigaru.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAshigaruDamage(spellLevel, entity));
            ashigaru.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getAshigaruHealth(spellLevel, entity));
            ashigaru.getAttributes().getInstance(Attributes.ARMOR).setBaseValue(this.getAshigaruArmor(spellLevel, entity));
            ashigaru.setHealth(ashigaru.getMaxHealth());
            float yrot = 6.281F / spellLevel * i + entity.getYRot() * (float) (Math.PI / 180.0);
            Vec3 spawn = Utils.moveToRelativeGroundLevel(world, entity.getEyePosition().add(new Vec3(radius * Mth.cos(yrot), 0.0, radius * Mth.sin(yrot))), 10);
            ashigaru.setPos(spawn.x, spawn.y, spawn.z);
            ashigaru.setYRot(entity.getYRot());
            ashigaru.setOldPosAndRot();
            SpiritAshigaruEntity creature = (SpiritAshigaruEntity)((SpellSummonEvent)NeoForge.EVENT_BUS
                  .post(new SpellSummonEvent(entity, ashigaru, this.spellId, spellLevel)))
               .getCreature();
            world.addFreshEntity(creature);
            SummonManager.initSummon(entity, creature, summonTime, summonedEntitiesCastData);
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recast.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getAshigaruDamage(int spellLevel, LivingEntity summoner) {
      return 2.0F + spellLevel / 2;
   }

   private float getAshigaruHealth(int spellLevel, LivingEntity summoner) {
      return 20 + spellLevel * this.spellPowerPerLevel;
   }

   private float getAshigaruArmor(int spellLevel, LivingEntity summoner) {
      return 2.0F + spellLevel / 2;
   }
}
