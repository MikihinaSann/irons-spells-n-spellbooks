package com.grandsigil.spell;

import com.grandsigil.GrandSigil;
import com.grandsigil.entity.ArenaEntity;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ColosseumSpell extends AbstractSpell {
   public static final ResourceLocation ID = GrandSigil.id("colosseum");
   private static final float RANGE = 34.0F;
   private static final float BASE_RADIUS = 34.0F;
   private static final float RADIUS_PER_LEVEL = 2.0F;
   private static final float TOLL = 0.2F;
   private final DefaultConfig config = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(210.0)
      .build();

   public ColosseumSpell() {
      this.baseManaCost = 400;
      this.manaCostPerLevel = 85;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 5;
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
      return new Vector3f(0.8F, 0.14F, 0.14F);
   }

   @Override
   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST;
   }

   public float radius(int spellLevel) {
      return 34.0F + 2.0F * (float)(spellLevel - 1);
   }

   @Override
   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float power = this.getSpellPower(spellLevel, caster);
      return List.of(
         Component.translatable("ui.grandsigil.colosseum.toll", Utils.stringTruncation(20.0, 0)),
         Component.translatable("ui.grandsigil.colosseum.cage"),
         Component.translatable("ui.grandsigil.colosseum.bleed", Utils.stringTruncation((double)(power * 0.2F), 1)),
         Component.translatable("ui.grandsigil.colosseum.feeds"),
         Component.translatable("ui.grandsigil.colosseum.rain", Utils.stringTruncation((double)(power * 0.22F), 1)),
         Component.translatable("ui.grandsigil.colosseum.thirst"),
         Component.translatable("ui.grandsigil.colosseum.collapse", Utils.stringTruncation((double)(power * 3.0F), 1)),
         Component.translatable("ui.grandsigil.colosseum.radius", Utils.stringTruncation((double)this.radius(spellLevel), 0))
      );
   }

   @Override
   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 target = Aim.ground(serverLevel, entity, 34.0F);
         payToll(serverLevel, entity);
         serverLevel.addFreshEntity(new ArenaEntity(serverLevel, target, entity, this.getSpellPower(spellLevel, entity), this.radius(spellLevel)));
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private static void payToll(ServerLevel level, LivingEntity caster) {
      if (caster instanceof Player player && player.getAbilities().instabuild) {
         return;
      }

      float owed = caster.getMaxHealth() * 0.2F;
      float shield = caster.getAbsorptionAmount();
      float fromShield = Math.min(shield, owed);
      caster.setAbsorptionAmount(shield - fromShield);
      owed -= fromShield;
      if (owed > 0.0F) {
         caster.setHealth(Math.max(1.0F, caster.getHealth() - owed));
      }

      level.playSound(null, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0F, 0.8F);
   }
}
