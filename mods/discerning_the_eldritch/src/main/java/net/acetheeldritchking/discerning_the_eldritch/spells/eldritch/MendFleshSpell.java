package net.acetheeldritchking.discerning_the_eldritch.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTEPotionEffectRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

public class MendFleshSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "mend_flesh");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(30.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)})
      );
   }

   public MendFleshSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 30;
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

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.MEND_FLESH_SOUND.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float healing = this.getHealAmount(entity, spellLevel);
      NeoForge.EVENT_BUS.post(new SpellHealEvent(entity, entity, healing, this.getSchoolType()));
      entity.heal(healing);
      entity.addEffect(new MobEffectInstance(DTEPotionEffectRegistry.MEND_FLESH_EFFECT, this.getEffectDuration(entity, spellLevel), 0, false, false, true));
      int count = 8;
      float radius = 0.25F;
      ASUtils.spawnParticlesInCircle(count, radius, 0.5F, 0.1F, entity, ParticleTypes.SCULK_SOUL);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getHealAmount(LivingEntity caster, int spellLevel) {
      return this.getSpellPower(spellLevel, caster) / 3.5F;
   }

   private int getEffectDuration(LivingEntity caster, int spellLevel) {
      return (int)(20.0F * this.getSpellPower(spellLevel, caster));
   }
}
