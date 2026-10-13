package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.ThreadsEntity;
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
import org.joml.Vector3f;

public class CrimsonThreadsSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("crimson_threads");
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public CrimsonThreadsSpell() {
      this.baseManaCost = 580;
      this.manaCostPerLevel = 115;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 6;
      this.castTime = 40;
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
      return new Vector3f(0.85F, 0.05F, 0.1F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.crimson_threads.reach", Utils.stringTruncation(40.0, 0), Utils.stringTruncation(8.0, 0)),
         Component.translatable(
            "ui.grandsigil.crimson_threads.drain", Utils.stringTruncation((double)(power * 0.15F * 2.0F), 1), Utils.stringTruncation(4.0, 0)
         ),
         Component.translatable("ui.grandsigil.crimson_threads.heal", Utils.stringTruncation(35.0, 0)),
         Component.translatable("ui.grandsigil.crimson_threads.snap", Utils.stringTruncation((double)(power * 1.2F), 1))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.addFreshEntity(new ThreadsEntity(serverLevel, entity, this.getSpellPower(spellLevel, entity)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
