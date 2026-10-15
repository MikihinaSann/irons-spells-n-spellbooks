package com.somake.somakespells.spells.symmetry;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.entity.PhantomBarrageProjectile;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PhantomBarrageSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "phantom_barrage");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.symmetrySchoolOrIce();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(10)
      .setCooldownSeconds(20.0)
      .build();

   public PhantomBarrageSpell() {
      this.manaCostPerLevel = 5;
      this.baseManaCost = 25;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
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
      return Optional.of((SoundEvent)SoundEvents.CROSSBOW_LOADING_MIDDLE.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getProjectileCount(spellLevel), 0)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      int count = this.getProjectileCount(spellLevel);
      float damage = this.getDamage(spellLevel, caster);
      float spread = 140.0F;
      float step = count > 1 ? spread / (count - 1) : 0.0F;
      float startAngle = -spread / 2.0F;
      Vec3 center = caster.position().add(0.0, caster.getBbHeight() + 1.5, 0.0);
      float facingYaw = caster.getYHeadRot();

      for (int i = 0; i < count; i++) {
         float angle = startAngle + step * i;
         double rad = Math.toRadians(facingYaw + angle + 180.0F);
         double xOffset = -Math.sin(rad) * 3.5;
         double zOffset = Math.cos(rad) * 3.5;
         double yOffset = (caster.getRandom().nextDouble() - 0.5) * 1.0;
         Vec3 spawnPos = center.add(xOffset, yOffset, zOffset);
         PhantomBarrageProjectile projectile = new PhantomBarrageProjectile(level, caster);
         projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
         projectile.setBaseDamage(damage);
         projectile.setWaitTimer(20 + i * 5);
         level.addFreshEntity(projectile);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private int getProjectileCount(int spellLevel) {
      return spellLevel + 2;
   }
}
