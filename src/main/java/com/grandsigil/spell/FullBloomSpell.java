package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.BloomEntity;
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

public class FullBloomSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("full_bloom");
   private static final float RANGE = 40.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public FullBloomSpell() {
      this.baseManaCost = 560;
      this.manaCostPerLevel = 110;
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
      return new Vector3f(0.5F, 0.95F, 0.45F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.full_bloom.field", Utils.stringTruncation(48.0, 0), Utils.stringTruncation(9.0, 0)),
         Component.translatable(
            "ui.grandsigil.full_bloom.wilt", Utils.stringTruncation((double)(power * 0.1F * 2.0F), 1), Utils.stringTruncation((double)(power * 0.35F), 1)
         ),
         Component.translatable("ui.grandsigil.full_bloom.heal"),
         Component.translatable(
            "ui.grandsigil.full_bloom.burst",
            Utils.stringTruncation((double)(power * 1.4F), 1),
            Utils.stringTruncation(30.0, 0),
            Utils.stringTruncation((double)(power * 0.3F), 1)
         )
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 40.0F);
         serverLevel.addFreshEntity(new BloomEntity(serverLevel, entity, target, this.getSpellPower(spellLevel, entity)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
