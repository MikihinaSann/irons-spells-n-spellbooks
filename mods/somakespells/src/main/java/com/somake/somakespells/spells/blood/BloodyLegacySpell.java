package com.somake.somakespells.spells.blood;

import com.somake.somakespells.entity.BloodyLegacyVisualEntity;
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
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

public class BloodyLegacySpell extends AbstractSpell {
   private static final ResourceLocation BLOOD_CAST_SOUND_ID = ResourceLocation.parse("irons_spellbooks:cast.generic.blood");
   private static final ResourceLocation BLOOD_IMPACT_SOUND_ID = ResourceLocation.parse("irons_spellbooks:spell.sacrifice.blood_explosion");
   private static final float MAX_RANGE = 12.0F;
   private static final float HIT_WIDTH = 1.35F;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "bloody_legacy");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(70.0)
      .build();

   public BloodyLegacySpell() {
      this.manaCostPerLevel = 8;
      this.baseSpellPower = 11;
      this.spellPowerPerLevel = 3;
      this.castTime = 16;
      this.baseManaCost = 40;
   }

   public boolean canBeInterrupted(Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
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
      return BuiltInRegistries.SOUND_EVENT.getOptional(BLOOD_CAST_SOUND_ID);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return BuiltInRegistries.SOUND_EVENT.getOptional(BLOOD_IMPACT_SOUND_ID);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 eyePosition = caster.getEyePosition();
         Vec3 forward = this.getHorizontalForward(caster);
         float range = this.getRange(serverLevel, eyePosition, forward);
         Vec3 lineStart = caster.position().add(0.0, 0.1, 0.0).add(forward.scale(1.2F));
         float damage = this.getDamage(spellLevel, caster);
         SpellDamageSource damageSource = this.getDamageSource(caster);
         this.spawnSlashVisual(serverLevel, caster, lineStart, forward, range, damage, damageSource);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getRange(Level level, Vec3 eyePosition, Vec3 forward) {
      Vec3 end = eyePosition.add(forward.scale(12.0));
      HitResult result = Utils.raycastForBlock(level, eyePosition, end, Fluid.NONE);
      return result.getType() == Type.BLOCK ? Mth.clamp((float)eyePosition.distanceTo(result.getLocation()), 2.0F, 12.0F) : 12.0F;
   }

   private void spawnSlashVisual(
      ServerLevel serverLevel, LivingEntity caster, Vec3 lineStart, Vec3 forward, float range, float damage, SpellDamageSource damageSource
   ) {
      Vec3 midPoint = lineStart.add(forward.scale(range * 0.5F));
      double groundY = this.findGroundY(serverLevel, midPoint.x, caster.getY() + 4.0, midPoint.z);
      BloodyLegacyVisualEntity visual = new BloodyLegacyVisualEntity(
         serverLevel, midPoint.x, groundY + 0.05, midPoint.z, caster.getYRot(), range, lineStart, damage, caster, damageSource
      );
      serverLevel.addFreshEntity(visual);
   }

   private Vec3 getHorizontalForward(LivingEntity caster) {
      Vec3 forward = caster.getLookAngle();
      Vec3 horizontal = new Vec3(forward.x, 0.0, forward.z);
      return horizontal.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 0.0, 1.0) : horizontal.normalize();
   }

   private double findGroundY(Level level, double x, double startY, double z) {
      Vec3 start = new Vec3(x, startY, z);
      Vec3 end = new Vec3(x, startY - 8.0, z);
      BlockHitResult result = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, CollisionContext.empty()));
      return result.getType() == Type.BLOCK ? result.getLocation().y : startY - 3.5;
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 5.0F + this.getSpellPower(spellLevel, caster) * 0.95F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setLifestealPercent(0.1F);
   }
}
