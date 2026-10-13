package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.GlacierEntity;
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

public class GlaciationSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("glaciation");
   private static final float RANGE = 40.0F;
   private static final float BASE_RADIUS = 32.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(190.0)
      .build();

   public GlaciationSpell() {
      this.baseManaCost = 620;
      this.manaCostPerLevel = 130;
      this.baseSpellPower = 22;
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
      return new Vector3f(0.42F, 0.78F, 0.96F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 32.0F + 2.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable(
            "ui.grandsigil.glaciation.wall", Utils.stringTruncation((double)(2.1F * this.radius(spellLevel)), 0), Utils.stringTruncation(42.0, 0)
         ),
         Component.translatable("ui.grandsigil.glaciation.warning", Utils.stringTruncation(3.0, 0)),
         Component.translatable("ui.grandsigil.glaciation.seal", Utils.stringTruncation(5.0, 0)),
         Component.translatable("ui.grandsigil.glaciation.stored"),
         Component.translatable("ui.grandsigil.glaciation.shatter", Utils.stringTruncation((double)(power * 4.0F), 1)),
         Component.translatable("ui.grandsigil.glaciation.shards", Utils.stringTruncation((double)(power * 1.5F), 1)),
         Component.translatable("ui.grandsigil.glaciation.freezes")
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 40.0F);
         serverLevel.addFreshEntity(new GlacierEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }
}
