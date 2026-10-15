package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.warphan.iss_magicfromtheeast.entity.spells.verdict_circle.VerdictCircle;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;
import net.warphan.iss_magicfromtheeast.spells.MFTESpellAnimations;

@AutoSpellConfig
public class UnderworldAidSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "underworld_aid");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(4)
      .setCooldownSeconds(160.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable(
            "ui.iss_magicfromtheeast.lost_health_damage_bonus", new Object[]{Utils.stringTruncation(this.getPercentBonus(spellLevel, caster) * 100.0F, 0)}
         ),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)})
      );
   }

   public UnderworldAidSpell() {
      this.manaCostPerLevel = 90;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
      this.baseManaCost = 180;
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
      return Optional.of((SoundEvent)SoundRegistry.RAISE_DEAD_START.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)MFTESoundRegistries.DEATH_BELL.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData pMagicData) {
      Utils.preCastTargetHelper(level, entity, pMagicData, this, 32, 15.0F, false);
      return true;
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 spawn = null;
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData) {
         LivingEntity target = castTargetingData.getTarget((ServerLevel)world);
         if (target != null) {
            spawn = target.position();
         }
      }

      if (spawn == null) {
         spawn = Utils.raycastForEntity(world, entity, 32.0F, true, 0.15F).getLocation();
         spawn = Utils.moveToRelativeGroundLevel(world, spawn, 6);
      }

      int duration = this.getDuration(spellLevel, entity);
      float radius = this.getRadius(spellLevel, entity);
      float percentBonus = this.getPercentBonus(spellLevel, entity);
      float pureDamage = this.getDamage(spellLevel, entity);
      VerdictCircle verdictCircle = new VerdictCircle(world);
      verdictCircle.setOwner(entity);
      verdictCircle.setCircular();
      verdictCircle.setRadius(radius);
      verdictCircle.setDuration(duration);
      verdictCircle.setDamage(pureDamage);
      verdictCircle.percentBonus = percentBonus;
      verdictCircle.setPos(spawn);
      world.addFreshEntity(verdictCircle);
      TargetedAreaEntity visualEntity = TargetedAreaEntity.createTargetAreaEntity(world, spawn, radius, 3216485);
      visualEntity.setDuration(duration);
      visualEntity.setOwner(verdictCircle);
      visualEntity.setShouldFade(true);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   private float getRadius(int spellLevel, LivingEntity caster) {
      return 4.0F;
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 80;
   }

   private float getPercentBonus(int spellLevel, LivingEntity caster) {
      return spellLevel / 10.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return MFTESpellAnimations.ANIMATION_CALLING_IMPERMANENCE;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }
}
