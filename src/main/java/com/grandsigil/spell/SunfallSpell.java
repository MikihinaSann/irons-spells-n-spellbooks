package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.SunEntity;
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

public class SunfallSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("sunfall");
   private static final float RANGE = 48.0F;
   private static final float BASE_RADIUS = 30.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public SunfallSpell() {
      this.baseManaCost = 550;
      this.manaCostPerLevel = 110;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 6;
      this.castTime = 60;
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
      return new Vector3f(1.0F, 0.62F, 0.18F);
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
         Component.translatable("ui.grandsigil.sunfall.impact", Utils.stringTruncation((double)(power * 4.2F), 1)),
         Component.translatable("ui.grandsigil.sunfall.scorch", Utils.stringTruncation((double)(power * 0.18F), 1)),
         Component.translatable("ui.grandsigil.sunfall.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0)),
         Component.translatable("ui.grandsigil.sunfall.delay", Utils.stringTruncation(7.0, 0)),
         Component.translatable("ui.grandsigil.sunfall.daylight"),
         Component.translatable("ui.grandsigil.sunfall.warning")
      );
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (!level.dimensionType().hasSkyLight()) {
         this.tell(entity, "message.grandsigil.no_sky");
         return false;
      } else {
         return true;
      }
   }

   private void tell(LivingEntity entity, String key) {
      if (!entity.level().isClientSide) {
         entity.sendSystemMessage(Component.translatable(key));
      }
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 48.0F);
         SunEntity sun = new SunEntity(
            serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel), serverLevel.getSunAngle(0.0F)
         );
         serverLevel.addFreshEntity(sun);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
