package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.IceAgeEntity;
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

public class IceAgeSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("ice_age");
   private static final float RANGE = 40.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(190.0)
      .build();

   public IceAgeSpell() {
      this.baseManaCost = 600;
      this.manaCostPerLevel = 120;
      this.baseSpellPower = 22;
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
      return List.of(
         Component.translatable("ui.grandsigil.ice_age.field", Utils.stringTruncation(56.0, 0), Utils.stringTruncation(9.0, 0)),
         Component.translatable("ui.grandsigil.ice_age.chill", Utils.stringTruncation((double)(power * 0.08F * 2.0F), 1)),
         Component.translatable("ui.grandsigil.ice_age.shards", 28, Utils.stringTruncation((double)(power * 0.55F), 1)),
         Component.translatable("ui.grandsigil.ice_age.shatter", Utils.stringTruncation((double)(power * 1.6F), 1), Utils.stringTruncation(35.000004F, 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 40.0F);
         serverLevel.addFreshEntity(new IceAgeEntity(serverLevel, entity, target, this.getSpellPower(spellLevel, entity)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
