package com.somake.somakespells.spells.aqua;

import com.somake.somakespells.entity.SummonedDrownedEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellSummonEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class SummonDrownedSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "summon_drowned");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(10)
      .setCooldownSeconds(60.0)
      .build();

   public SummonDrownedSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
      this.baseManaCost = 50;
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
      return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{Utils.stringTruncation(this.getHealth(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this)) {
         SummonedEntitiesCastData summonedEntitiesCastData = new SummonedEntitiesCastData();
         int summonTime = 12000;
         SummonedDrownedEntity drowned = new SummonedDrownedEntity((EntityType<? extends Drowned>)ModEntityTypes.SUMMONED_DROWNED.get(), level);
         drowned.setPos(entity.position());
         if (drowned.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
            drowned.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getDamage(spellLevel, entity));
         }

         if (drowned.getAttributes().hasAttribute(Attributes.MAX_HEALTH)) {
            drowned.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getHealth(spellLevel, entity));
         }

         drowned.setHealth(drowned.getMaxHealth());
         SummonedDrownedEntity creature = (SummonedDrownedEntity)((SpellSummonEvent)NeoForge.EVENT_BUS
               .post(new SpellSummonEvent(entity, drowned, this.spellId, spellLevel)))
            .getCreature();
         level.addFreshEntity(creature);
         SummonManager.initSummon(entity, creature, summonTime, summonedEntitiesCastData);
         RecastInstance recastInstance = new RecastInstance(
            this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), summonTime, castSource, summonedEntitiesCastData
         );
         recasts.addRecast(recastInstance, playerMagicData);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
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

   private float getHealth(int spellLevel, LivingEntity caster) {
      return (28 + spellLevel * 6) * this.getEntityPowerMultiplier(caster);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
