package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.effect.OakskinEffect;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.entity.sound.AngelEntity;
import net.alshanex.alshanex_familiars.entity.sound.HealingCircleEntity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings1Entity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings2Entity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings3Entity;
import net.alshanex.alshanex_familiars.entity.sound.ult_strings.UltStrings4Entity;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.alshanex_familiars.registry.EntityRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class AngelSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "guardian_angel");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(FSchoolRegistry.SOUND_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(90.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)}),
         Component.translatable("ui.alshanex_familiars.regen", new Object[]{Utils.stringTruncation(1 + Math.min(2, this.getAmplifier(spellLevel, caster)), 1)}),
         Component.translatable("ui.irons_spellbooks.damage_reduction", new Object[]{Utils.stringTruncation(this.getPercentDamage(spellLevel, caster), 0)}),
         Component.translatable(
               "attribute.modifier.take.1",
               new Object[]{Utils.stringTruncation(spellLevel * 0.2F * 100.0F, 0), Component.translatable("attribute.name.generic.movement_speed")}
            )
            .withStyle(ChatFormatting.RED)
      );
   }

   public AngelSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 30;
      this.baseManaCost = 80;
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
      return Optional.of((SoundEvent)AFSoundRegistry.HEALING_AURA.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)AFSoundRegistry.ANGEL_CAST.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      float radius = this.getRadius(entity);
      TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, entity.position(), radius, Utils.packRGB(this.getTargetingColor()), entity);
      playerMagicData.setAdditionalCastData(new TargetAreaCastData(entity.position(), area));
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      AngelEntity angel = new AngelEntity((EntityType<?>)EntityRegistry.ANGEL.get(), level);
      angel.setPos(entity.position().x, entity.position().y + 3.0, entity.position().z);
      HealingCircleEntity healingCircle = new HealingCircleEntity(level, this.getAmplifier(spellLevel, entity), entity);
      healingCircle.setDuration(300);
      healingCircle.setRadius(this.getRadius(entity));
      healingCircle.setCircular();
      healingCircle.moveTo(entity.position());
      level.addFreshEntity(angel);
      level.addFreshEntity(healingCircle);
      UltStrings1Entity strings1 = new UltStrings1Entity(level, entity);
      strings1.moveTo(entity.position());
      level.addFreshEntity(strings1);
      if (this.getAmplifier(spellLevel, entity) >= 1) {
         UltStrings2Entity strings2 = new UltStrings2Entity(level, entity);
         strings2.moveTo(entity.position());
         level.addFreshEntity(strings2);
      }

      if (this.getAmplifier(spellLevel, entity) >= 2) {
         UltStrings3Entity strings3 = new UltStrings3Entity(level, entity);
         strings3.moveTo(entity.position());
         level.addFreshEntity(strings3);
      }

      if (this.getAmplifier(spellLevel, entity) >= 3) {
         UltStrings4Entity strings4 = new UltStrings4Entity(level, entity);
         strings4.moveTo(entity.position());
         level.addFreshEntity(strings4);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getRadius(LivingEntity caster) {
      return 4.0F;
   }

   public int getAmplifier(int spellLevel, LivingEntity caster) {
      return spellLevel - 1;
   }

   private float getPercentDamage(int spellLevel, LivingEntity entity) {
      return OakskinEffect.getReductionAmount(spellLevel - 1, null) * 100.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CAST_KNEELING_PRAYER;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SELF_CAST_TWO_HANDS;
   }
}
