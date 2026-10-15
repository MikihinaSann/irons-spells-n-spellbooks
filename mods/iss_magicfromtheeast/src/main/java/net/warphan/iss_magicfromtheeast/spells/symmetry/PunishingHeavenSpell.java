package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.warphan.iss_magicfromtheeast.entity.mobs.jade_executioner.JadeExecutionerEntity;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;

@AutoSpellConfig
public class PunishingHeavenSpell extends AbstractSpell {
   private final ResourceLocation spellID = new ResourceLocation("iss_magicfromtheeast", "punishing_heaven");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(320.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{this.getExecutionerHealth(spellLevel, null)}),
         Component.translatableEscape("ui.irons_spellbooks.damage", new Object[]{this.getExecutionerDamage(spellLevel, null)}),
         Component.translatable("ui.iss_magicfromtheeast.armor", new Object[]{this.getExecutionerArmor(spellLevel, null)})
      );
   }

   public PunishingHeavenSpell() {
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 5;
      this.castTime = 30;
      this.baseManaCost = 250;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellID;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.RAISE_DEAD_START.value());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.BEACON_ACTIVATE);
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
      int summonTime = 3600;
      PlayerRecasts recast = playerMagicData.getPlayerRecasts();
      if (!recast.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
         Vec3 location = Utils.getTargetBlock(world, entity, Fluid.ANY, 12.0).getLocation();
         JadeExecutionerEntity jadeExecutioner = new JadeExecutionerEntity(world, entity, true);
         jadeExecutioner.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getExecutionerDamage(spellLevel, entity));
         jadeExecutioner.getAttributes().getInstance(Attributes.ARMOR).setBaseValue(this.getExecutionerArmor(spellLevel, entity));
         jadeExecutioner.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getExecutionerHealth(spellLevel, entity));
         jadeExecutioner.setHealth(jadeExecutioner.getMaxHealth());
         jadeExecutioner.setPos(location);
         jadeExecutioner.setOnGround(true);
         JadeExecutionerEntity creature = (JadeExecutionerEntity)((SpellSummonEvent)NeoForge.EVENT_BUS
               .post(new SpellSummonEvent(entity, jadeExecutioner, this.spellID, spellLevel)))
            .getCreature();
         world.addFreshEntity(creature);
         SummonManager.initSummon(entity, creature, summonTime, summonedEntitiesCastData);
         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recast.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getExecutionerDamage(int spellLevel, LivingEntity summoner) {
      return 8 + (this.spellPowerPerLevel - 1) * spellLevel;
   }

   private float getExecutionerHealth(int spellLevel, LivingEntity summoner) {
      return 250 + this.spellPowerPerLevel * spellLevel * 10;
   }

   private float getExecutionerArmor(int spellLevel, LivingEntity summoner) {
      return 6.0F + spellLevel * 2;
   }

   public AnimationHolder getCastStartAnimation() {
      return MFTESpellAnimations.ANIMATION_INVOKING;
   }
}
