package com.somake.somakespells.spells.fire;

import com.somake.somakespells.entity.IgnisShieldEntity;
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
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IgnisShieldSpell extends AbstractSpell {
   private static final float BASE_RETALIATION_DAMAGE = 1.5F;
   private static final float RETALIATION_DAMAGE_PER_POWER = 0.2F;
   private static final float BASE_RETALIATION_KNOCKBACK = 1.2F;
   private static final float RETALIATION_KNOCKBACK_PER_POWER = 0.05F;
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "ignis_shield");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(60.0)
      .build();

   public IgnisShieldSpell() {
      this.manaCostPerLevel = 20;
      this.baseManaCost = 50;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 0;
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
      return Optional.of((SoundEvent)SoundEvents.ARMOR_EQUIP_NETHERITE.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.SELF_CAST_ANIMATION;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.hp", new Object[]{Utils.stringTruncation(this.getShieldHP(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getRetaliationDamage(spellLevel, caster), 1)})
      );
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      IgnisShieldEntity shield = new IgnisShieldEntity(level, this.getShieldHP(spellLevel, caster));
      shield.setOwner(caster);
      shield.configureRetaliation(this.getRetaliationDamage(spellLevel, caster), this.getRetaliationKnockback(spellLevel, caster));
      Vec3 spawn = Utils.raycastForEntity(level, caster, 3.0F, true).getLocation();
      shield.setPos(spawn);
      shield.setRotation(caster.getXRot(), caster.getYRot());
      level.addFreshEntity(shield);
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getShieldHP(int spellLevel, LivingEntity caster) {
      return 50.0F + this.getSpellPower(spellLevel, caster) * 5.0F;
   }

   private float getRetaliationDamage(int spellLevel, LivingEntity caster) {
      return 1.5F + this.getSpellPower(spellLevel, caster) * 0.2F;
   }

   private float getRetaliationKnockback(int spellLevel, LivingEntity caster) {
      return 1.2F + this.getSpellPower(spellLevel, caster) * 0.05F;
   }
}
