package net.acetheeldritchking.cataclysm_spellbooks.spells.ender;

import com.github.L_Ender.cataclysm.entity.effect.Void_Vortex_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GravityStormSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "gravity_storm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(40.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.gravity_storm.lifespan", new Object[]{this.getVortexLifeSpan(spellLevel, caster) / 20}),
         Component.translatable("ui.cataclysm_spellbooks.range", new Object[]{this.getRangeForSpell(spellLevel)})
      );
   }

   public GravityStormSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 5;
      this.castTime = 30;
      this.baseManaCost = 100;
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
      return Optional.of((SoundEvent)ModSounds.BLACK_HOLE_LOOP.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.BLACK_HOLE_CLOSING.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      double casterEye = entity.getEyeY();
      double casterX = entity.getX();
      double casterZ = entity.getZ();
      HitResult result = Utils.raycastForEntity(level, entity, this.getRangeForSpell(spellLevel), true);
      Vec3 gravityStormLocation = result.getLocation();
      Level casterLevel = entity.level();
      Void_Vortex_Entity voidVortex = new Void_Vortex_Entity(casterLevel, casterX, casterEye, casterZ, 0.0F, entity, 10);
      voidVortex.setLifespan(this.getVortexLifeSpan(spellLevel, entity));
      voidVortex.moveTo(gravityStormLocation);
      level.addFreshEntity(voidVortex);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getVortexLifeSpan(int spellLevel, LivingEntity caster) {
      return (int)this.getSpellPower(spellLevel, caster) * 20;
   }

   private int getRangeForSpell(int spellLevel) {
      return 12 * spellLevel;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.ANIMATION_LONG_CAST_FINISH;
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
