package net.acetheeldritchking.discerning_the_eldritch.spells.ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.glacial_edge.GlacialEdge;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.acetheeldritchking.discerning_the_eldritch.utils.DTETags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GlacialEdgeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "glacial_edge");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(25.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.discerning_the_eldritch.frozen_weapon_bonus")
      );
   }

   public GlacialEdgeSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
      this.baseManaCost = 55;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_RUDE_BUSTER_SLASH;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.GLACIAL_CLEAVE_FULL.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      GlacialEdge glacialEdge = new GlacialEdge(level, entity);
      glacialEdge.setPos(entity.position().add(0.0, entity.getEyeHeight() - glacialEdge.getBoundingBox().getYsize() * 0.5, 0.0));
      glacialEdge.shootFromRotation(entity, entity.getXRot(), entity.getYHeadRot(), 0.0F, glacialEdge.getSpeed(), 1.0F);
      if (entity.getMainHandItem().is(DTETags.FROZEN_WEAPONS)) {
         glacialEdge.setDamage(this.getDamage(spellLevel, entity) * 1.5F);
      } else {
         glacialEdge.setDamage(this.getDamage(spellLevel, entity));
      }

      level.addFreshEntity(glacialEdge);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return (float)(this.getSpellPower(spellLevel, caster) * 0.25);
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFreezeTicks(80);
   }
}
