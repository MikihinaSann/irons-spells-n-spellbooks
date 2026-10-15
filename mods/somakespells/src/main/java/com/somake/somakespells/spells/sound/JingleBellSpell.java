package com.somake.somakespells.spells.sound;

import com.somake.somakespells.compat.TunesCompat;
import com.somake.somakespells.config.Config;
import com.somake.somakespells.entity.BellEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;

public class JingleBellSpell extends AbstractSpell {
   private static final int PULSE_INTERVAL = 28;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "jingle_bell");
   private final DefaultConfig defaultConfig;

   public JingleBellSpell() {
      this.manaCostPerLevel = 8;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
      this.castTime = 10;
      this.baseManaCost = 34;
      this.defaultConfig = new DefaultConfig()
         .setMinRarity(SpellRarity.RARE)
         .setSchoolResource(TunesCompat.schoolOrEvocation())
         .setMaxLevel(5)
         .setCooldownSeconds(14.0)
         .build();
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamagePerPulse(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel), 1)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getLifetimeTicks(spellLevel), 1)})
      );
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
      return Optional.of(SoundEvents.BELL_BLOCK);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return TunesCompat.getHarpWaveSound().or(() -> Optional.of(SoundEvents.BELL_RESONATE));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         Vec3 eyePosition = caster.getEyePosition();
         Vec3 target = Utils.raycastForBlock(level, eyePosition, eyePosition.add(caster.getLookAngle().scale(24.0)), Fluid.NONE).getLocation();
         double groundY = this.getGroundY(level, target.x, target.y + 1.5, target.z);
         Vec3 circlePosition = new Vec3(target.x, groundY + 0.01, target.z);
         BellEntity bell = new BellEntity(
            level,
            caster,
            circlePosition.x,
            circlePosition.y,
            circlePosition.z,
            this.getRadius(spellLevel),
            this.getDamagePerPulse(spellLevel, caster),
            this.getLifetimeTicks(spellLevel),
            28
         );
         level.addFreshEntity(bell);
         TunesCompat.spawnCelestialHealingCircle(
            level, caster, this.getHealingAmplifier(spellLevel), this.getLifetimeTicks(spellLevel), this.getRadius(spellLevel), circlePosition
         );
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getRadius(int spellLevel) {
      return 3.5F + 0.35F * Mth.clamp(spellLevel - 1, 0, 4);
   }

   private float getDamagePerPulse(int spellLevel, LivingEntity caster) {
      return 4.0F + this.getSpellPower(spellLevel, caster) * 0.8F;
   }

   private int getHealingAmplifier(int spellLevel) {
      return Mth.clamp(spellLevel - 1, 0, 4);
   }

   private int getLifetimeTicks(int spellLevel) {
      return 180 + 20 * Mth.clamp(spellLevel - 1, 0, 4);
   }

   private double getGroundY(Level level, double x, double startY, double z) {
      Vec3 start = new Vec3(x, startY, z);
      Vec3 end = new Vec3(x, startY - 20.0, z);
      BlockHitResult result = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, CollisionContext.empty()));
      return result.getType() == Type.BLOCK ? result.getLocation().y : startY - 1.0;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public boolean allowLooting() {
      return !(Boolean)Config.DISABLE_JINGLE_BELL_CRAFTING_AND_LOOTING.get();
   }

   public boolean allowCrafting() {
      return !(Boolean)Config.DISABLE_JINGLE_BELL_CRAFTING_AND_LOOTING.get();
   }
}
