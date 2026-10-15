package com.somake.somakespells.spells.lightning;

import com.somake.somakespells.entity.LightningLanceDanceProjectile;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningDanceSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_dance");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(20.0)
      .build();

   public LightningDanceSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 3;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 50;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSpellPower(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getSwordCount(spellLevel, caster), 1)})
      );
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

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int swordCount = this.getSwordCount(spellLevel, entity);
      Vec3 center = entity.getEyePosition().add(0.0, 1.5, 0.0);
      float headYaw = entity.getYHeadRot();
      float range = 180.0F;
      float step = swordCount > 1 ? range / (swordCount - 1) : 0.0F;
      float start = swordCount > 1 ? -range / 2.0F : 0.0F;

      for (int i = 0; i < swordCount; i++) {
         float degreeOffset = start + step * i;
         double rad = Math.toRadians(headYaw + degreeOffset);
         double x = -Math.sin(rad) * 1.5;
         double z = Math.cos(rad) * 1.5;
         Vec3 spawnPos = center.add(x, 0.0, z);
         LightningLanceDanceProjectile projectile = new LightningLanceDanceProjectile(level, entity);
         projectile.setDamage(this.getSpellPower(spellLevel, entity));
         projectile.setWaitTimer(120);
         projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
         projectile.setDeltaMovement(Vec3.ZERO);
         projectile.setYRot(headYaw);
         projectile.setXRot(entity.getXRot());
         projectile.setForcedRotation(headYaw, entity.getXRot());
         level.addFreshEntity(projectile);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getSwordCount(int spellLevel, LivingEntity entity) {
      return spellLevel + 2;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }
}
