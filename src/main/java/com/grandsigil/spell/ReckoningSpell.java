package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.ReckoningEntity;
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

public class ReckoningSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("reckoning");
   private static final float RANGE = 40.0F;
   private static final float BASE_RADIUS = 12.0F;
   private static final float RADIUS_PER_LEVEL = 1.0F;
   private static final int PATROL = 10;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(180.0)
      .build();

   public ReckoningSpell() {
      this.baseManaCost = 560;
      this.manaCostPerLevel = 120;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 6;
      this.castTime = 100;
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
      return new Vector3f(1.0F, 0.95F, 0.72F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 12.0F + 1.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.reckoning.blow", Utils.stringTruncation((double)(power * 4.6F), 1)),
         Component.translatable("ui.grandsigil.reckoning.undead", Utils.stringTruncation((double)(power * 4.6F * 2.5F), 1)),
         Component.translatable("ui.grandsigil.reckoning.burn", Utils.stringTruncation((double)(power * 0.2F * 2.0F), 1)),
         Component.translatable("ui.grandsigil.reckoning.duration", Utils.stringTruncation(7.0, 0)),
         Component.translatable("ui.grandsigil.reckoning.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0))
      );
   }

   @Override
   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         this.light(serverLevel, spellLevel, entity);
      }

      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   @Override
   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel && entity.tickCount % 10 == 0) {
         this.light(serverLevel, spellLevel, entity);
      }

      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 40.0F);
         ReckoningEntity star = ReckoningEntity.held(serverLevel, entity);
         if (star == null) {
            star = this.light(serverLevel, spellLevel, entity);
         }

         if (star != null) {
            star.release(target);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   @Override
   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      if (cancelled && level instanceof ServerLevel serverLevel) {
         ReckoningEntity star = ReckoningEntity.held(serverLevel, entity);
         if (star != null) {
            star.fizzle();
         }
      }

      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
   }

   private ReckoningEntity light(ServerLevel level, int spellLevel, LivingEntity entity) {
      ReckoningEntity existing = ReckoningEntity.held(level, entity);
      if (existing != null) {
         return existing;
      } else {
         ReckoningEntity star = new ReckoningEntity(
            level, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel), this.getEffectiveCastTime(spellLevel, entity)
         );
         level.addFreshEntity(star);
         return star;
      }
   }
}
