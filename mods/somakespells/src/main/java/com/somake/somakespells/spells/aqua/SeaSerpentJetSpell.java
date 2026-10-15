package com.somake.somakespells.spells.aqua;

import com.somake.somakespells.entity.SeaSerpentJetEntity;
import com.somake.somakespells.registries.ModSchools;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;

public class SeaSerpentJetSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "sea_serpent_jet");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(5)
      .setCooldownSeconds(8.0)
      .build();

   public SeaSerpentJetSpell() {
      this.baseManaCost = 40;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 16;
      this.spellPowerPerLevel = 3;
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
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_THUNDER.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.TOUCH_GROUND_ANIMATION;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         HitResult result = Utils.raycastForBlock(level, caster.getEyePosition(), caster.getEyePosition().add(caster.getLookAngle().scale(32.0)), Fluid.NONE);
         Vec3 targetPos = result.getLocation();
         float damage = this.getDamage(spellLevel, caster);
         double groundY = this.getGroundY(level, targetPos.x, targetPos.y + 1.0, targetPos.z);
         SeaSerpentJetEntity jet = new SeaSerpentJetEntity(level, targetPos.x, groundY, targetPos.z, caster.getYRot(), damage, caster);
         level.addFreshEntity(jet);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private double getGroundY(Level level, double x, double startY, double z) {
      Vec3 start = new Vec3(x, startY, z);
      Vec3 end = new Vec3(x, startY - 20.0, z);
      BlockHitResult result = level.clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, CollisionContext.empty()));
      return result.getType() == Type.BLOCK ? result.getLocation().y : startY - 1.0;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
