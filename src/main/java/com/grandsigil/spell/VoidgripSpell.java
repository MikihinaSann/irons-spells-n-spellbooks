package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.GripEntity;
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

public class VoidgripSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("voidgrip");
   private static final float RANGE = 32.0F;
   private static final float BASE_RADIUS = 30.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(220.0)
      .build();

   public VoidgripSpell() {
      this.baseManaCost = 680;
      this.manaCostPerLevel = 140;
      this.baseSpellPower = 24;
      this.spellPowerPerLevel = 7;
      this.castTime = 70;
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
      return new Vector3f(0.66F, 0.26F, 0.96F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 30.0F + 2.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.voidgrip.isles"),
         Component.translatable("ui.grandsigil.voidgrip.lift", Utils.stringTruncation(30.0, 0)),
         Component.translatable("ui.grandsigil.voidgrip.hold", Utils.stringTruncation(3.0, 0)),
         Component.translatable("ui.grandsigil.voidgrip.slam", Utils.stringTruncation((double)(power * 4.8F), 1)),
         Component.translatable("ui.grandsigil.voidgrip.splash", Utils.stringTruncation((double)(power * 1.3F), 1)),
         Component.translatable("ui.grandsigil.voidgrip.window"),
         Component.translatable("ui.grandsigil.voidgrip.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 32.0F);
         serverLevel.addFreshEntity(new GripEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
