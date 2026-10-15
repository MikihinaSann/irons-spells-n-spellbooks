package net.acetheeldritchking.cataclysm_spellbooks.spells.sand;

import com.github.L_Ender.cataclysm.entity.effect.Sandstorm_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSPotionEffectRegistry;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PharaohsWrathSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "pharaohs_wrath");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(CSSchoolRegistry.SAND_RESOURCE)
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
         Component.translatable(
            "attribute.modifier.plus.1",
            new Object[]{
               Utils.stringTruncation(this.getPercentageSpellPower(spellLevel, caster), 0), Component.translatable("attribute.irons_spellbooks.spell_power")
            }
         ),
         Component.translatable("ui.cataclysm_spellbooks.kings_wrath_strikes")
      );
   }

   public PharaohsWrathSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
      this.baseManaCost = 95;
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

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.REMNANT_ROAR.get());
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_WRATH_ROAR;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.addEffect(
         new MobEffectInstance(
            CSPotionEffectRegistry.KINGS_WRATH_EFFECT, (int)(this.getSpellPower(spellLevel, entity) * 40.0F), spellLevel - 1, false, false, true
         )
      );

      for (int i = 0; i < 3; i++) {
         double casterX = entity.getX();
         double casterY = entity.getY();
         double casterZ = entity.getZ();
         float angle = (float)(i * Math.PI / 1.5);
         double stormX = casterX + Mth.cos(angle) * 4.0F;
         double stormZ = casterZ + Mth.sin(angle) * 4.0F;
         Sandstorm_Entity sandstorm = new Sandstorm_Entity(level, stormX, casterY, stormZ, (int)(this.getSpellPower(spellLevel, entity) * 40.0F), angle, entity);
         level.addFreshEntity(sandstorm);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getPercentageAttackDamage(int spellLevel, LivingEntity caster) {
      return spellLevel * 0.2F * 100.0F;
   }

   private float getPercentageSpellPower(int spellLevel, LivingEntity caster) {
      return spellLevel * 0.05F * 100.0F;
   }
}
