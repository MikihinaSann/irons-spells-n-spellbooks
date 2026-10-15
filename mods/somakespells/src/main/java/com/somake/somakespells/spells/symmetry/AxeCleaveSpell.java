package com.somake.somakespells.spells.symmetry;

import com.github.L_Ender.cataclysm.entity.projectile.Axe_Blade_Entity;
import com.somake.somakespells.compat.MagicFromTheEastCompat;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
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

public class AxeCleaveSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "axe_cleave");
   private final ResourceLocation symmetrySchool = MagicFromTheEastCompat.symmetrySchoolOrIce();
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(this.symmetrySchool)
      .setMaxLevel(8)
      .setCooldownSeconds(10.0)
      .build();

   public AxeCleaveSpell() {
      this.manaCostPerLevel = 10;
      this.baseManaCost = 30;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 2;
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
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_THROW.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         float damage = this.getDamage(spellLevel, caster);
         Vec3 look = caster.getLookAngle();
         look = new Vec3(look.x, 0.0, look.z).normalize();
         Axe_Blade_Entity axe = new Axe_Blade_Entity(caster, look.x * 3.0, 0.0, look.z * 3.0, level, damage, caster.getYRot());
         axe.setPos(caster.getX(), caster.getY() + 0.5, caster.getZ());
         axe.setDeltaMovement(look.scale(3.0));
         level.addFreshEntity(axe);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
