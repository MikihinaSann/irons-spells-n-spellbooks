package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.CoffinEntity;
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

public class BlackCoffinSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("black_coffin");
   private static final float RANGE = 32.0F;
   private static final float BASE_HALF = 4.5F;
   private static final float HALF_PER_LEVEL = 0.5F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(190.0)
      .build();

   public BlackCoffinSpell() {
      this.baseManaCost = 580;
      this.manaCostPerLevel = 120;
      this.baseSpellPower = 22;
      this.spellPowerPerLevel = 7;
      this.castTime = 90;
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
      return new Vector3f(0.72F, 0.28F, 0.98F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float half(int spellLevel) {
      return 4.5F + 0.5F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.black_coffin.spear", 24, Utils.stringTruncation((double)(power * 0.28F), 1)),
         Component.translatable("ui.grandsigil.black_coffin.rupture", Utils.stringTruncation((double)(power * 2.5F), 1)),
         Component.translatable("ui.grandsigil.black_coffin.hold"),
         Component.translatable("ui.grandsigil.black_coffin.pull"),
         Component.translatable(
            "ui.grandsigil.black_coffin.size",
            Utils.stringTruncation((double)(this.half(spellLevel) * 2.0F), 0),
            Utils.stringTruncation((double)(this.half(spellLevel) * 2.6F), 0)
         )
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 32.0F);
         serverLevel.addFreshEntity(new CoffinEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.half(spellLevel)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
