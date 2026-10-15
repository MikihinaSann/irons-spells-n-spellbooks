package com.somake.somakespells.spells.symmetry;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.DesertWrathControllerEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DesertWrathSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "desert_wrath");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.symmetrySchoolOrIce();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(5)
      .setCooldownSeconds(60.0)
      .build();

   public DesertWrathSpell() {
      this.manaCostPerLevel = 20;
      this.baseManaCost = 100;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
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
      return Optional.of(SoundEvents.SAND_STEP);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{spellLevel}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         DesertWrathControllerEntity controller = new DesertWrathControllerEntity(
            (EntityType<?>)ModEntityTypes.DESERT_WRATH_CONTROLLER.get(),
            level,
            caster,
            this.getDamage(spellLevel, caster),
            this.getDuration(spellLevel, caster),
            spellLevel
         );
         level.addFreshEntity(controller);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 20 * (10 + spellLevel * 2);
   }
}
