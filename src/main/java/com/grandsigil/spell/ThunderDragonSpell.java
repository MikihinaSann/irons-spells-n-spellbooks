package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.ThunderDragonEntity;
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

public class ThunderDragonSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("thunder_dragon");
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(190.0)
      .build();

   public ThunderDragonSpell() {
      this.baseManaCost = 580;
      this.manaCostPerLevel = 115;
      this.baseSpellPower = 20;
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
      return new Vector3f(0.55F, 0.85F, 1.0F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      float first = power * 0.3F;
      float last = first * 2.32F;
      return List.of(
         Component.translatable("ui.grandsigil.thunder_dragon.strikes", 12, Utils.stringTruncation((double)first, 1), Utils.stringTruncation((double)last, 1)),
         Component.translatable("ui.grandsigil.thunder_dragon.body", Utils.stringTruncation((double)(power * 0.15F * 4.0F), 1)),
         Component.translatable("ui.grandsigil.thunder_dragon.finale", Utils.stringTruncation((double)(power * 3.0F), 1), Utils.stringTruncation(30.0, 0)),
         Component.translatable("ui.grandsigil.thunder_dragon.reach", Utils.stringTruncation(64.0, 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.addFreshEntity(new ThunderDragonEntity(serverLevel, entity, this.getSpellPower(spellLevel, entity)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
