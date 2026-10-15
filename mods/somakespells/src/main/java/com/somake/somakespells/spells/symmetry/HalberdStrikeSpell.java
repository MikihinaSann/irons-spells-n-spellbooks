package com.somake.somakespells.spells.symmetry;

import com.github.L_Ender.cataclysm.entity.projectile.Phantom_Halberd_Entity;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class HalberdStrikeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "halberd_strike");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.symmetrySchoolOrIce();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(8)
      .setCooldownSeconds(12.0)
      .build();

   public HalberdStrikeSpell() {
      this.manaCostPerLevel = 5;
      this.baseManaCost = 30;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
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
      return Optional.of(SoundEvents.EVOKER_PREPARE_ATTACK);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         float damage = this.getDamage(spellLevel, caster);
         float yaw = caster.getYRot();
         this.spawnHalberdLine(level, caster, yaw, damage, 0);
         this.spawnHalberdLine(level, caster, yaw - 30.0F, damage, 5);
         this.spawnHalberdLine(level, caster, yaw + 30.0F, damage, 5);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private void spawnHalberdLine(Level level, LivingEntity caster, float yaw, float damage, int initialDelay) {
      int distance = 10;
      float spacing = 1.5F;
      double rad = Math.toRadians(yaw + 90.0F);
      double xVec = Math.cos(rad);
      double zVec = Math.sin(rad);

      for (int i = 1; i <= distance; i++) {
         double x = caster.getX() + xVec * (i * spacing);
         double z = caster.getZ() + zVec * (i * spacing);
         BlockPos pos = new BlockPos((int)x, (int)caster.getY(), (int)z);
         double y = this.findGround(level, pos);
         int delay = initialDelay + i * 2;
         Phantom_Halberd_Entity halberd = new Phantom_Halberd_Entity(level, x, y, z, (float)Math.toRadians(yaw), delay, caster, damage);
         level.addFreshEntity(halberd);
         if (level instanceof ServerLevel serverLevel) {
            double d0 = halberd.getRandomX(1.0);
            double d1 = halberd.getRandomY();
            double d2 = halberd.getRandomZ(1.0);
            serverLevel.sendParticles((ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get(), d0, d1, d2, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }
   }

   private double findGround(Level level, BlockPos pos) {
      for (int i = 0; i < 10; i++) {
         BlockPos check = pos.below(i);
         if (level.getBlockState(check).isFaceSturdy(level, check, Direction.UP)) {
            return check.getY() + 1.0;
         }
      }

      return pos.getY();
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
