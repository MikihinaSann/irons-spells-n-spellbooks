package com.somake.somakespells.spells.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.SoulReprisalDaggerEntity;
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
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.ThrownPhantomDaggerEntity;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoulReprisalSpell extends AbstractSpell {
   private static final int DAGGER_COUNT = 6;
   private static final float DAGGER_SPREAD = 60.0F;
   private static final int BASE_LIFETIME_TICKS = 92;
   private static final int MIN_LIFETIME_TICKS = 112;
   private static final int MAX_LIFETIME_TICKS = 400;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "soul_reprisal");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(MagicFromTheEastCompat.spiritSchoolOrEvocation())
      .setMaxLevel(8)
      .setCooldownSeconds(18.0)
      .build();

   public SoulReprisalSpell() {
      this.baseManaCost = 38;
      this.manaCostPerLevel = 6;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
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
         Component.translatable("ui.somakespells.damage_per_hit", new Object[]{Utils.stringTruncation(this.getDaggerDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(6.0, 0)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDaggerLifetime(spellLevel, caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         RedSoulVisuals.spawnSpiritCastPulse(serverLevel, caster);
         this.spawnDaggers(
            serverLevel, caster, this.getDaggerDamage(spellLevel, caster), this.getDaggerLifetime(spellLevel, caster), RedSoulHelper.isActive(caster)
         );
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private void spawnDaggers(ServerLevel level, LivingEntity caster, float damage, int lifetimeTicks, boolean redSoul) {
      Vec3 look = caster.getLookAngle().normalize();
      float step = 12.0F;
      float startAngle = -30.0F;
      Vec3 spawnPosition = caster.getEyePosition().add(look.scale(0.45));

      for (int i = 0; i < 6; i++) {
         Vec3 direction = MathUtils.rotateYaw(look, startAngle + step * i).normalize();
         SoulReprisalDaggerEntity dagger = new SoulReprisalDaggerEntity(
            (EntityType<? extends ThrownPhantomDaggerEntity>)ModEntityTypes.SOUL_REPRISAL_DAGGER.get(), level
         );
         dagger.setPosRaw(spawnPosition.x, spawnPosition.y, spawnPosition.z);
         dagger.setReturnEntity(caster);
         dagger.setReturnTick(12);
         dagger.setSpellDamage(damage);
         dagger.setLifetimeTicks(lifetimeTicks);
         dagger.shoot(direction.x, direction.y, direction.z, 1.15F, 0.0F);
         dagger.setOwner(caster);
         dagger.setRed(redSoul);
         level.addFreshEntity(dagger);
      }
   }

   private float getDaggerDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.32F + 1.0F;
   }

   private int getDaggerLifetime(int spellLevel, LivingEntity caster) {
      float spiritSpellPower = this.getSpellPower(spellLevel, caster);
      return Mth.clamp(Math.round(92.0F + spiritSpellPower * 2.0F), 112, 400);
   }
}
