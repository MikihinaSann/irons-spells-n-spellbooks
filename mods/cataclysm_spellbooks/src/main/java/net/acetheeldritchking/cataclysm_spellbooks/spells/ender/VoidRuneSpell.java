package net.acetheeldritchking.cataclysm_spellbooks.spells.ender;

import com.github.L_Ender.cataclysm.entity.projectile.Void_Rune_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class VoidRuneSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "void_rune");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.void_rune", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel), 1)})
      );
   }

   public VoidRuneSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return super.getCastStartSound();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.VOID_RUNE_RISING.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 50, 0.15F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetingData) {
         LivingEntity targetEntity = targetingData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            double targetX = targetEntity.getX();
            double targetY = targetEntity.getY();
            double targetZ = targetEntity.getZ();
            BlockPos blockPos = new BlockPos((int)targetX, (int)targetY, (int)targetZ);

            for (int i = 0; i < this.getDuration(spellLevel, entity); i++) {
               double d1 = targetY + 1.0;
               float f = (float)Mth.atan2(targetZ, targetX);
               float f1 = (float)(f + 1.2566370801612687);
               int delay = i / 3;
               Void_Rune_Entity voidRune = new Void_Rune_Entity(
                  level,
                  targetX + Mth.cos(f1) * 1.5,
                  blockPos.getY() + targetY,
                  targetZ + Mth.sin(f1) * 1.5,
                  (float)targetY,
                  delay,
                  this.getDamage(spellLevel),
                  entity
               );
               level.addFreshEntity(voidRune);
               targetEntity.addEffect(
                  new MobEffectInstance(
                     CSPotionEffectRegistry.SUMMON_VOID_RUNE, this.getEffectDuration(spellLevel, entity), spellLevel - 1, false, false, false
                  )
               );
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return this.getLevelFor(spellLevel, caster);
   }

   private int getEffectDuration(int spellPower, LivingEntity caster) {
      int duration = (int)(this.getSpellPower(spellPower, caster) * 100.0F) / 2;
      int maxTicksForDuration = 200;
      return Math.min(duration * 20, maxTicksForDuration);
   }

   private float getDamage(int spellLevel) {
      return spellLevel;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }
}
