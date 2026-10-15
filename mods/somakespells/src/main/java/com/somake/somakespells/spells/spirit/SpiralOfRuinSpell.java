package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SpiralOfRuinProjectileEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpiralOfRuinSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "spiral_of_ruin");
   private final ResourceLocation spiritSchool = MagicFromTheEastCompat.spiritSchoolOrEvocation();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(this.spiritSchool)
      .setMaxLevel(8)
      .setCooldownSeconds(20.0)
      .build();

   public SpiralOfRuinSpell() {
      this.manaCostPerLevel = 7;
      this.baseManaCost = 44;
      this.baseSpellPower = 9;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
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
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getExplosionDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getExplosionCount(spellLevel), 0)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         SpiralOfRuinProjectileEntity projectile = new SpiralOfRuinProjectileEntity(
            serverLevel,
            caster,
            this.getDirectDamage(spellLevel, caster),
            this.getExplosionDamage(spellLevel, caster),
            this.getExplosionCount(spellLevel),
            this.getMaxRadius(spellLevel),
            RedSoulHelper.isActive(caster)
         );
         Vec3 direction = caster.getLookAngle();
         projectile.setPos(caster.getX() + direction.x * 0.8, caster.getEyeY() - 0.15, caster.getZ() + direction.z * 0.8);
         projectile.shoot(direction);
         serverLevel.addFreshEntity(projectile);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDirectDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.7F + 2.0F;
   }

   private float getExplosionDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.52F + 1.5F;
   }

   private int getExplosionCount(int spellLevel) {
      return Mth.clamp(12 + spellLevel * 2, 14, 24);
   }

   private float getMaxRadius(int spellLevel) {
      return 5.0F + spellLevel * 0.45F;
   }
}
