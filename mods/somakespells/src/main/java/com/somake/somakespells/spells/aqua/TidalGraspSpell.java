package com.somake.somakespells.spells.aqua;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.IABossMonsters.Scylla.Scylla_Ceraunus_Entity;
import com.somake.somakespells.entity.SpellAnchorEntity;
import com.somake.somakespells.registries.ModEntityTypes;
import com.somake.somakespells.registries.ModSchools;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TidalGraspSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "tidal_grasp");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(ModSchools.AQUA.getId())
      .setMaxLevel(6)
      .setCooldownSeconds(12.0)
      .build();

   public TidalGraspSpell() {
      this.baseManaCost = 40;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 6;
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
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_RIPTIDE_1.value());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_THUNDER.value());
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_INSTANT_CAST;
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, caster, playerMagicData, this, this.getRange(spellLevel), 0.2F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetData) {
         LivingEntity targetEntity = targetData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            SpellAnchorEntity anchor = new SpellAnchorEntity((EntityType<? extends Scylla_Ceraunus_Entity>)ModEntityTypes.SPELL_ANCHOR.get(), level, caster);
            anchor.setHomingTarget(targetEntity);
            anchor.setDamageSpell(this);
            double d0 = targetEntity.getX() - caster.getX();
            double d1 = targetEntity.getY(0.5) - anchor.getY();
            double d2 = targetEntity.getZ() - caster.getZ();
            anchor.shoot(d0, d1, d2, 3.0F, 0.0F);
            anchor.setBaseDamage(this.getDamage(spellLevel, caster));
            level.addFreshEntity(anchor);
         }
      }

      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(this.getRange(spellLevel), 1)})
      );
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private int getRange(int spellLevel) {
      return 10 + spellLevel * 4;
   }
}
