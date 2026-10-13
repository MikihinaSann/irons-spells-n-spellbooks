package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.PhoenixEntity;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PhoenixSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("phoenix");
   private static final float RANGE = 48.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public PhoenixSpell() {
      this.baseManaCost = 560;
      this.manaCostPerLevel = 115;
      this.baseSpellPower = 21;
      this.spellPowerPerLevel = 6;
      this.castTime = 50;
   }

   @Override
   public ResourceLocation getSpellResource() {
      return ID;
   }

   @Override
   public DefaultConfig getDefaultConfig() {
      return this.config;
   }

   @Override
   public CastType getCastType() {
      return CastType.LONG;
   }

   @Override
   public Vector3f getTargetingColor() {
      return new Vector3f(1.0F, 0.45F, 0.08F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.phoenix.dive", Utils.stringTruncation((double)(power * 2.4F), 1), Utils.stringTruncation(16.0, 0)),
         Component.translatable("ui.grandsigil.phoenix.rings", Utils.stringTruncation((double)(power * 0.14F * 2.0F), 1)),
         Component.translatable("ui.grandsigil.phoenix.wings", Utils.stringTruncation((double)(power * 0.35F), 1)),
         Component.translatable("ui.grandsigil.phoenix.laps", Utils.stringTruncation(34.0, 0), Utils.stringTruncation(20.0, 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 48.0F);
         serverLevel.addFreshEntity(new PhoenixEntity(serverLevel, entity, target, this.getSpellPower(spellLevel, entity)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
