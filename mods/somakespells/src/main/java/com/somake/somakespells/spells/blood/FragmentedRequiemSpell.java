package com.somake.somakespells.spells.blood;

import com.somake.somakespells.entity.BloodmarkMarkerEntity;
import com.somake.somakespells.entity.FragmentedSwordRainEntity;
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
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FragmentedRequiemSpell extends AbstractSpell {
   private static final float BASE_RADIUS = 3.0F;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "fragmented_requiem");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(16.0)
      .build();

   public FragmentedRequiemSpell() {
      this.baseManaCost = 40;
      this.manaCostPerLevel = 6;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 15;
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
      return Optional.of(SoundEvents.AMETHYST_BLOCK_BREAK);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getSwordDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(3.0, 1)}),
         Component.translatable("ui.irons_spellbooks.projectile_count", new Object[]{Utils.stringTruncation(this.getSwordCount(spellLevel), 0)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 targetCenter = this.getTargetCenter(serverLevel, caster);
         float damage = this.getSwordDamage(spellLevel, caster);
         int swordCount = this.getSwordCount(spellLevel);
         this.spawnSwordCircle(serverLevel, caster, targetCenter, damage, swordCount);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private Vec3 getTargetCenter(ServerLevel level, LivingEntity caster) {
      Vec3 casterCenter = new Vec3(caster.getX(), caster.getBoundingBox().minY + 0.1, caster.getZ());
      return FragmentedSwordRainEntity.findSpawnPoint(level, casterCenter);
   }

   private void spawnSwordCircle(ServerLevel level, LivingEntity caster, Vec3 center, float damage, int swordCount) {
      double angleStep = (Math.PI * 2) / swordCount;

      for (int i = 0; i < swordCount; i++) {
         double angle = angleStep * i;
         double x = center.x + Math.cos(angle) * 3.0;
         double z = center.z + Math.sin(angle) * 3.0;
         Vec3 spawn = FragmentedSwordRainEntity.findSpawnPoint(level, new Vec3(x, center.y, z));
         level.addFreshEntity(new BloodmarkMarkerEntity(level, spawn.x, spawn.y, spawn.z, FragmentedSwordRainEntity.totalLifetime()));
         FragmentedSwordRainEntity sword = new FragmentedSwordRainEntity(level, caster, spawn, damage);
         sword.setGroundY((float)spawn.y);
         level.addFreshEntity(sword);
      }
   }

   private int getSwordCount(int spellLevel) {
      return Mth.clamp(6 + spellLevel, 7, 14);
   }

   private float getSwordDamage(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return power * 0.9F + 2.0F;
   }
}
