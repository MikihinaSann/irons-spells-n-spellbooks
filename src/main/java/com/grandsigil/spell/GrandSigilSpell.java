package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.config.GsConfig;
import com.grandsigil.entity.SigilEntity;
import com.grandsigil.fx.GsFx;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class GrandSigilSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("grand_sigil");
   private static final float RANGE = 32.0F;
   private static final float BASE_RADIUS = 14.0F;
   private static final float RADIUS_PER_LEVEL = 1.5F;
   public static final float CENTRE_FRACTION = 0.25F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(170.0)
      .build();

   public GrandSigilSpell() {
      this.baseManaCost = 520;
      this.manaCostPerLevel = 110;
      this.baseSpellPower = 30;
      this.spellPowerPerLevel = 10;
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
      return GsFx.GOLD;
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 14.0F + 1.5F * (float)(spellLevel - 1);
   }

   @Override
   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Aim.target(level, caster, 32.0F) != null;
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.info.bind"),
         Component.translatable("ui.grandsigil.info.ritual", GsConfig.COMMON.chargeSeconds.get(), 12),
         Component.translatable("ui.grandsigil.info.damage", Utils.stringTruncation((double)(power * 8.0F), 1)),
         Component.translatable("ui.grandsigil.info.single"),
         Component.translatable("ui.grandsigil.info.broken"),
         Component.translatable("ui.grandsigil.info.radius", Utils.stringTruncation((double)this.radius(spellLevel), 1))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         LivingEntity victim = Aim.target(serverLevel, entity, 32.0F);
         if (victim != null) {
            serverLevel.getEntitiesOfClass(SigilEntity.class, entity.getBoundingBox().inflate(96.0), sigil -> sigil.fedBy(entity)).forEach(Entity::discard);
            Vec3 floor = Aim.beneath(serverLevel, victim);
            serverLevel.addFreshEntity(
               new SigilEntity(
                  serverLevel,
                  floor,
                  entity,
                  victim,
                  this.getSpellPower(spellLevel, entity),
                  this.radius(spellLevel),
                  (Integer)GsConfig.COMMON.chargeSeconds.get() * 20 - 40
               )
            );
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
