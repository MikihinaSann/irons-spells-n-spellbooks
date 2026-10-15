package net.alshanex.alshanex_familiars.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import java.util.List;
import java.util.Optional;
import net.alshanex.alshanex_familiars.entity.sound.HarpExplosionEntity;
import net.alshanex.alshanex_familiars.registry.AFSoundRegistry;
import net.alshanex.familiarslib.registry.FSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ExplosionMelodySpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("alshanex_familiars", "explosion_melody");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(FSchoolRegistry.SOUND_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(caster), 1)})
      );
   }

   public ExplosionMelodySpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
      this.baseManaCost = 60;
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
      return Optional.of((SoundEvent)AFSoundRegistry.HARP_EXPLOSION_CAST.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)AFSoundRegistry.CLEF.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      float radius = this.getRadius(entity);
      HitResult hitResult = Utils.raycastForEntity(level, entity, 32.0F, true, 0.2F);
      TargetedAreaEntity area = TargetedAreaEntity.createTargetAreaEntity(level, hitResult.getLocation(), radius, Utils.packRGB(this.getTargetingColor()));
      playerMagicData.setAdditionalCastData(
         new TargetAreaCastData(hitResult instanceof EntityHitResult entityHit ? entityHit.getEntity().position() : hitResult.getLocation(), area)
      );
      return true;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetAreaCastData castData) {
         Vec3 targetArea = castData.getCenter();
         float radius = castData.getCastingEntity().getRadius();
         float damage = this.getDamage(spellLevel, entity);
         HarpExplosionEntity explosion = new HarpExplosionEntity(level, entity, damage);
         explosion.setDuration(60);
         explosion.setRadius(radius);
         explosion.setCircular();
         explosion.moveTo(targetArea);
         level.addFreshEntity(explosion);
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return (5.0F + 5.0F * this.getSpellPower(spellLevel, caster)) * 0.7F;
   }

   public int getRadius(LivingEntity caster) {
      return 4;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.CAST_KNEELING_PRAYER;
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SELF_CAST_TWO_HANDS;
   }
}
