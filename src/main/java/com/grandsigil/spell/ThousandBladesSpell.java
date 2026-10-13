package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.ArsenalEntity;
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

public class ThousandBladesSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("thousand_blades");
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(175.0)
      .build();

   public ThousandBladesSpell() {
      this.baseManaCost = 540;
      this.manaCostPerLevel = 115;
      this.baseSpellPower = 22;
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
      return new Vector3f(1.0F, 0.82F, 0.36F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.thousand_blades.blades", 120, Utils.stringTruncation((double)(power * 0.075F), 1)),
         Component.translatable("ui.grandsigil.thousand_blades.hunt"),
         Component.translatable("ui.grandsigil.thousand_blades.duration", Utils.stringTruncation(8.0, 0)),
         Component.translatable("ui.grandsigil.thousand_blades.reach", Utils.stringTruncation(48.0, 0)),
         Component.translatable("ui.grandsigil.thousand_blades.colossus", Utils.stringTruncation((double)(power * 1.6F), 1), Utils.stringTruncation(11.0, 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.addFreshEntity(new ArsenalEntity(serverLevel, entity, this.getSpellPower(spellLevel, entity), entity.getYRot()));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
