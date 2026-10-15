package net.acetheeldritchking.cataclysm_spellbooks.spells.ice;

import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ForgoneRageSpell extends AbstractMaledictusSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "forgone_rage");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(120.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getSpellPower(spellLevel, caster) * 20.0F, 1)}),
         Component.translatable(
            "attribute.modifier.plus.1",
            new Object[]{
               Utils.stringTruncation(this.getPercentageAttackDamage(spellLevel, caster), 0), Component.translatable("attribute.name.generic.attack_damage")
            }
         ),
         Component.translatable("ui.cataclysm_spellbooks.forgone_rage_strikes")
      );
   }

   public ForgoneRageSpell() {
      this.manaCostPerLevel = 25;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
      this.baseManaCost = 55;
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

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.MALEDICTUS_BATTLE_CRY.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_WRATH_ROAR;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.addEffect(
         new MobEffectInstance(CSPotionEffectRegistry.WRATHFUL, (int)(this.getSpellPower(spellLevel, entity) * 40.0F), spellLevel - 1, false, false, true)
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFreezeTicks(100);
   }

   private float getPercentageAttackDamage(int spellLevel, LivingEntity caster) {
      return spellLevel * 0.2F * 100.0F;
   }
}
