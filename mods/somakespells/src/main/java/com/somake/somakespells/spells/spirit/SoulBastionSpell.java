package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SoulBastionControllerEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.util.RedSoulHelper;
import com.somake.somakespells.util.RedSoulVisuals;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SoulBastionSpell extends AbstractSpell {
   private static final int SHIELD_COUNT = 8;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "soul_bastion");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(8)
      .setCooldownSeconds(24.0)
      .build();

   public SoulBastionSpell() {
      this.manaCostPerLevel = 7;
      this.baseManaCost = 50;
      this.baseSpellPower = 9;
      this.spellPowerPerLevel = 1;
      this.castTime = 22;
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
      return MagicFromTheEastCompat.getSpiritCastSound();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getShieldDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(8.0, 0)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         SoulBastionControllerEntity controller = new SoulBastionControllerEntity(
            (EntityType<?>)ModEntityTypes.SOUL_BASTION_CONTROLLER.get(),
            serverLevel,
            caster,
            this.getShieldDamage(spellLevel, caster),
            RedSoulHelper.isActive(caster)
         );
         serverLevel.addFreshEntity(controller);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getShieldDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) + 2.0F;
   }
}
