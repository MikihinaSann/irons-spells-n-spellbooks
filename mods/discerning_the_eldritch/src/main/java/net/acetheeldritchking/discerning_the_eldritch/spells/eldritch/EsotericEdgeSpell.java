package net.acetheeldritchking.discerning_the_eldritch.spells.eldritch;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.esoteric_edge.EsotericEdge;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EsotericEdgeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "esoteric_edge");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(25.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.translatable("ui.discerning_the_eldritch.extra_damage")
      );
   }

   public EsotericEdgeSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
      this.baseManaCost = 70;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public AnimationHolder getCastStartAnimation() {
      return ASSpellAnimations.ANIMATION_RIGHT_HORIZONTAL_SWORD_SLASH;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.ESOTERIC_EDGE_SLASH_2.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      EsotericEdge esotericEdge = new EsotericEdge(level, entity);
      esotericEdge.setPos(entity.position().add(0.0, entity.getEyeHeight() - esotericEdge.getBoundingBox().getYsize() * 0.5, 0.0));
      esotericEdge.shootFromRotation(entity, entity.getXRot(), entity.getYHeadRot(), 0.0F, esotericEdge.getSpeed(), 1.0F);
      esotericEdge.setDamage(this.getDamage(spellLevel, entity));
      level.addFreshEntity(esotericEdge);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return (float)(this.getSpellPower(spellLevel, caster) * 0.77 + this.getWeaponDamage(caster));
   }

   private float getWeaponDamage(LivingEntity caster) {
      return Utils.getWeaponDamage(caster);
   }

   private String getDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float weaponDamage = Utils.getWeaponDamage(caster);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }
}
