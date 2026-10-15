package net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Dimensional_Rift_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DimensionalRiftSpell extends AbstractAbyssalSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "dimensional_rift");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(CSSchoolRegistry.ABYSSAL_RESOURCE)
      .setMaxLevel(6)
      .setCooldownSeconds(120.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.dimensional_rift_spell.lifespan", new Object[]{this.getRiftLifespan(spellLevel, caster) / 20})
      );
   }

   public DimensionalRiftSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 40;
      this.baseManaCost = 500;
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

   public Optional<SoundEvent> getCastStartSound() {
      return super.getCastStartSound();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.BLACK_HOLE_OPENING.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      double casterEye = entity.getEyeY();
      double casterX = entity.getX();
      double casterZ = entity.getZ();
      HitResult result = Utils.raycastForEntity(level, entity, 32.0F, true);
      Vec3 dimensionRiftLocation = result.getLocation();
      Level casterLevel = entity.level();
      Dimensional_Rift_Entity dimensionalRift = new Dimensional_Rift_Entity(casterLevel, casterX, casterEye, casterZ, entity);
      dimensionalRift.setStage(this.setRiftStage(spellLevel, dimensionalRift));
      dimensionalRift.setLifespan(this.getRiftLifespan(spellLevel, entity));
      dimensionalRift.moveTo(dimensionRiftLocation);
      CameraShakeManager.addCameraShake(new CameraShakeData(25, entity.position(), 25.0F));
      if (!casterLevel.isClientSide) {
         level.addFreshEntity(dimensionalRift);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public int getRiftLifespan(int spellLevel, LivingEntity caster) {
      int lifespanPerLevel = (int)(this.getSpellPower(spellLevel, caster) * 100.0F);
      int baseLevel = 100;
      return lifespanPerLevel - baseLevel;
   }

   public int setRiftStage(int spellLevel, Dimensional_Rift_Entity rift) {
      if (rift.getStage() < 4) {
         rift.setStage(rift.getStage() + spellLevel);
      }

      return rift.getStage();
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SLASH_ANIMATION;
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
