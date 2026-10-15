package net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Blast_Entity;
import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class AbyssalBlastSpell extends AbstractAbyssalSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "abyssal_blast");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(CSSchoolRegistry.ABYSSAL_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(450.0)
      .build();

   public AbyssalBlastSpell() {
      this.manaCostPerLevel = 100;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 15;
      this.castTime = 60;
      this.baseManaCost = 800;
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

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.ABYSS_BLAST_ONLY_CHARGE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.ABYSS_BLAST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      double casterX = entity.getX();
      double casterY = CSUtils.getEyeHeight(entity);
      double casterZ = entity.getZ();
      float dir = 90.0F;
      float casterXRot = (float)(-(entity.getXRot() * Math.PI / 180.0));
      float casterYHeadRot = (float)((entity.getYHeadRot() + dir) * Math.PI / 180.0);
      ScreenShake_Entity.ScreenShake(level, entity.position(), 15.0F, 0.2F, 20, 40);
      if (!level.isClientSide) {
         entity.addEffect(new MobEffectInstance(CSPotionEffectRegistry.INCAPACITATED_EFFECT, 85, 2, true, true, true));
         Abyss_Blast_Entity abyss_blast = new Abyss_Blast_Entity(
            (EntityType)ModEntities.ABYSS_BLAST.get(),
            level,
            entity,
            casterX,
            casterY,
            casterZ,
            casterYHeadRot,
            casterXRot,
            80,
            dir,
            this.getDamage(spellLevel, entity),
            this.getHPDamage(spellLevel)
         );
         level.addFreshEntity(abyss_blast);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 1.0F + this.getSpellPower(spellLevel, caster);
   }

   private float getHPDamage(int spellLevel) {
      return spellLevel * 10 / 100.0F;
   }
}
