package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.entity.blue_ball.BlueBallEntity;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SlumberNoteSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "slumber_note");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(8.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{"5s"})
      );
   }

   public SlumberNoteSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 6;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 20;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TSoundRegistry.BALL_THROW.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      BlueBallEntity ball = new BlueBallEntity(world, entity);
      ball.setPos(entity.position().add(0.0, entity.getEyeHeight() - ball.getBoundingBox().getYsize() * 0.5, 0.0));
      ball.shoot(entity.getLookAngle());
      ball.setDamage(this.getDamage(spellLevel, entity));
      world.addFreshEntity(ball);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.5F;
   }
}
