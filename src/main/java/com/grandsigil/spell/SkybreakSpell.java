package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.BoltEntity;
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

public class SkybreakSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("skybreak");
   private static final float RANGE = 48.0F;
   private static final float BASE_RADIUS = 40.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(200.0)
      .build();

   public SkybreakSpell() {
      this.baseManaCost = 600;
      this.manaCostPerLevel = 120;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 6;
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
      return new Vector3f(0.72F, 0.86F, 1.0F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_RAISED_HAND;
   }

   public float radius(int spellLevel) {
      return 40.0F + 2.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.skybreak.strike", Utils.stringTruncation((double)(power * 3.5F), 1)),
         Component.translatable("ui.grandsigil.skybreak.column", Utils.stringTruncation((double)(power * 5.2F), 1), 16),
         Component.translatable("ui.grandsigil.skybreak.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0)),
         Component.translatable("ui.grandsigil.skybreak.delay", Utils.stringTruncation(3.5, 1)),
         Component.translatable("ui.grandsigil.skybreak.shelter")
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 48.0F);
         BoltEntity bolt = new BoltEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel));
         serverLevel.addFreshEntity(bolt);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
