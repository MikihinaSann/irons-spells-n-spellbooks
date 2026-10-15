package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.effects.EndMayhemEffect;
import net.alshanex.alshanex_familiars.registry.EffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EndMayhemSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "end_mayhem");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(120.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable(
            "ui.irons_spellbooks.damage",
            new Object[]{Utils.stringTruncation(EndMayhemEffect.getDamageFromAmplifier(this.getAmplifierForLevel(spellLevel, caster), caster), 2)}
         ),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{10}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDurationTicks(spellLevel, caster), 1)})
      );
   }

   public EndMayhemSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
      this.baseManaCost = 70;
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

   public boolean allowCrafting() {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.ENDER_CAST.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      entity.addEffect(
         new MobEffectInstance(
            EffectRegistry.MAYHEM, this.getDurationTicks(spellLevel, entity), this.getAmplifierForLevel(spellLevel, entity), false, false, true
         )
      );
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private int getAmplifierForLevel(int spellLevel, LivingEntity caster) {
      return 8 + (int)((spellLevel - 1) * this.getEntityPowerMultiplier(caster));
   }

   public int getDurationTicks(int spellLevel, LivingEntity caster) {
      return (int)((20.0F + 2 * (spellLevel - 1) * this.getEntityPowerMultiplier(caster)) * 20.0F);
   }
}
