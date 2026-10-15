package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EncoreSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "encore");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(90.0)
      .build();

   public EncoreSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 0;
      this.castTime = 10;
      this.baseManaCost = 100;
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
      return Optional.of((SoundEvent)TSoundRegistry.HARP_SYMPHONY.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.35F);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)world);
         if (targetEntity != null) {
            targetEntity.addEffect(new MobEffectInstance(TEffectRegistry.ENCORE, this.getDuration(spellLevel, entity), 0));
         }
      }

      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return 600;
   }
}
