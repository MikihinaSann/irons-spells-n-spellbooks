package com.somake.somakespells.spells.lightning;

import com.github.L_Ender.cataclysm.entity.projectile.Lightning_Spear_Entity;
import com.somake.somakespells.entity.SpellLightningSpearEntity;
import com.somake.somakespells.registries.ModEntityTypes;
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
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LightningSpearSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "lightning_spear");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(5.0)
      .build();

   public LightningSpearSpell() {
      this.baseManaCost = 40;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 8;
      this.spellPowerPerLevel = 1;
      this.castTime = 15;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.LIGHTNING_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of(SoundEvents.LIGHTNING_BOLT_THUNDER);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         Vec3 look = caster.getLookAngle();
         SpellLightningSpearEntity spear = new SpellLightningSpearEntity(
            (EntityType<? extends Lightning_Spear_Entity>)ModEntityTypes.SPELL_LIGHTNING_SPEAR.get(),
            caster,
            caster.getX(),
            caster.getEyeY() - 0.1,
            caster.getZ(),
            look,
            this.getDamage(spellLevel, caster),
            level
         );
         spear.setAreaDamage(this.getDamage(spellLevel, caster) * 0.5F);
         spear.setAreaRadius(3.0F);
         spear.setSpawnCloud(false);
         level.addFreshEntity(spear);
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}));
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }
}
