package net.warphan.iss_magicfromtheeast.spells.symmetry;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
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
import net.warphan.iss_magicfromtheeast.entity.spells.bagua_array.BaguaCircle;
import net.warphan.iss_magicfromtheeast.registries.MFTESchoolRegistries;
import net.warphan.iss_magicfromtheeast.registries.MFTESoundRegistries;

@AutoSpellConfig
public class BaguaArrayCircleSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("iss_magicfromtheeast", "bagua_array_circle");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(MFTESchoolRegistries.SYMMETRY_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(60.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.iss_magicfromtheeast.undead_damaging", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 4)}),
         Component.translatable("ui.iss_magicfromtheeast.reverse_healing", new Object[]{Utils.stringTruncation(this.getHealingPercent(spellLevel, caster), 0)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.duration", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 1)})
      );
   }

   public BaguaArrayCircleSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 70;
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
      return Optional.of((SoundEvent)MFTESoundRegistries.SYMMETRY_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.HOLY_CAST.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 15.0F, false);
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
      BaguaCircle baguaCircle = new BaguaCircle(world);
      baguaCircle.setAmplifier(spellLevel - 1);
      baguaCircle.setOwner(entity);
      baguaCircle.setCircular();
      baguaCircle.setRadius(radius);
      baguaCircle.setDuration(duration);
      baguaCircle.setDamage(this.getDamage(spellLevel, entity));
      baguaCircle.setPos(spawn);
      world.addFreshEntity(baguaCircle);
      TargetedAreaEntity visualEntity = TargetedAreaEntity.createTargetAreaEntity(world, spawn, radius, 41836);
      visualEntity.setDuration(duration);
      visualEntity.setOwner(baguaCircle);
      visualEntity.setShouldFade(true);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.5F;
   }

   private float getRadius(int spellLevel, LivingEntity caster) {
      return 5.0F;
   }

   private int getDuration(int spellLevel, LivingEntity caster) {
      return 200;
   }

   private int getHealingPercent(int spellLevel, LivingEntity caster) {
      return spellLevel * 10;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.ANIMATION_CONTINUOUS_OVERHEAD;
   }
}
