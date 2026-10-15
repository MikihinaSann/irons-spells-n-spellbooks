package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
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
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.alshanex.alshanex_familiars.entity.misc.BlueBirdEntity;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;

public class BirdsSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "birds_spell");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(FSchoolRegistry.SOUND_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(80.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.summon_count", new Object[]{spellLevel}),
         Component.translatable(
            "ui.irons_spellbooks.hastened", new Object[]{Utils.stringTruncation(Math.max(1, this.getEffectAmplifier(spellLevel, caster)) * 0.1F * 100.0F, 1)}
         ),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)})
      );
   }

   public BirdsSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 3;
      this.castTime = 30;
      this.baseManaCost = 50;
   }

   public boolean allowLooting() {
      return false;
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
      return Optional.of((SoundEvent)AFSoundRegistry.BIRDS_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)AFSoundRegistry.BIRDS_SPAWN.get());
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
         float radius = 1.5F + 0.185F * spellLevel;
         int summonTime = this.getDuration(spellLevel, entity);

         for (int i = 0; i < spellLevel; i++) {
            BlueBirdEntity bird = new BlueBirdEntity(world, this.getEffectAmplifier(spellLevel, entity));
            SummonManager.initSummon(entity, bird, summonTime, summonedEntitiesCastData);
            float yrot = 6.281F / spellLevel * i + entity.getYRot() * (float) (Math.PI / 180.0);
            Vec3 spawn = Utils.moveToRelativeGroundLevel(world, entity.getEyePosition().add(new Vec3(radius * Mth.cos(yrot), 0.0, radius * Mth.sin(yrot))), 10);
            bird.setPos(spawn.x, spawn.y, spawn.z);
            bird.setYRot(entity.getYRot());
            bird.setOldPosAndRot();
            SpellSummonEvent<BlueBirdEntity> event = (SpellSummonEvent<BlueBirdEntity>)NeoForge.EVENT_BUS
               .post(new SpellSummonEvent(entity, bird, this.spellId, spellLevel));
            world.addFreshEntity(event.getCreature());
         }

         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public int getEffectAmplifier(int spellLevel, LivingEntity caster) {
      return spellLevel - 1;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return 1200;
   }
}
