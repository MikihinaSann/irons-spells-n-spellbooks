package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import java.util.Optional;
import net.alshanex.tunes_n_tomes.entity.fortissimo.FortissimoReleaseEntity;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FortissimoSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "fortissimo");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(45.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getDuration(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 2)})
      );
   }

   public FortissimoSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 3;
      this.castTime = 0;
      this.baseManaCost = 50;
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
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      float radius = this.getRadius(spellLevel, entity);
      FortissimoReleaseEntity release = new FortissimoReleaseEntity(level);
      release.moveTo(entity.position());
      level.addFreshEntity(release);
      level.getEntities(
            entity,
            entity.getBoundingBox().inflate(radius, 4.0, radius),
            target -> !DamageSources.isFriendlyFireBetween(target, entity) && Utils.hasLineOfSight(level, entity, target, true)
         )
         .forEach(
            target -> {
               if (target instanceof LivingEntity livingEntity && livingEntity.distanceToSqr(entity) < radius * radius) {
                  livingEntity.addEffect(new MobEffectInstance(TEffectRegistry.DEAFENED, this.getDuration(spellLevel, entity)));
                  Vec3 origin = entity.position();
                  double dx = livingEntity.getX() - origin.x;
                  double dz = livingEntity.getZ() - origin.z;
                  double distance = Math.sqrt(dx * dx + dz * dz);
                  double knockbackStrength = 1.5F + (spellLevel - 1) * 0.21428572F;
                  double normalizedX = dx / distance;
                  double normalizedZ = dz / distance;
                  double scale = 1.0 - distance / radius;
                  double knockbackX = normalizedX * knockbackStrength * scale;
                  double knockbackZ = normalizedZ * knockbackStrength * scale;
                  double knockbackY = 0.4 * scale;
                  livingEntity.setDeltaMovement(
                     livingEntity.getDeltaMovement().x + knockbackX,
                     livingEntity.getDeltaMovement().y + knockbackY,
                     livingEntity.getDeltaMovement().z + knockbackZ
                  );
                  livingEntity.hurtMarked = true;
               }
            }
         );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 6.0F + spellLevel * 0.75F;
   }

   public int getDuration(int spellLevel, LivingEntity caster) {
      return (int)(this.getSpellPower(spellLevel, caster) * 20.0F);
   }

   public AnimationHolder getCastFinishAnimation() {
      return SpellAnimations.SLASH_ANIMATION;
   }
}
