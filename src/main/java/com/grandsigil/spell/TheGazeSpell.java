package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.GazeEntity;
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

public class TheGazeSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("the_gaze");
   private static final float RANGE = 44.0F;
   private static final float BASE_RADIUS = 45.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(150.0)
      .build();

   public TheGazeSpell() {
      this.baseManaCost = 460;
      this.manaCostPerLevel = 90;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 5;
      this.castTime = 80;
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
      return new Vector3f(0.62F, 0.3F, 0.94F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 45.0F + 2.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.grandsigil.the_gaze.transfix"),
         Component.translatable("ui.grandsigil.the_gaze.duration", Utils.stringTruncation(14.0, 0)),
         Component.translatable("ui.grandsigil.the_gaze.nodamage"),
         Component.translatable("ui.grandsigil.the_gaze.price"),
         Component.translatable("ui.grandsigil.the_gaze.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 44.0F);
         serverLevel.addFreshEntity(new GazeEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
