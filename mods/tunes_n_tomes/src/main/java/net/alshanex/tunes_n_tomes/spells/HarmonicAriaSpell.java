package net.alshanex.tunes_n_tomes.spells;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.alshanex.tunes_n_tomes.data.HarmonicAriaCastData;
import net.alshanex.tunes_n_tomes.entity.aria_beam.AriaBeamEntity;
import net.alshanex.tunes_n_tomes.registry.TEffectRegistry;
import net.alshanex.tunes_n_tomes.registry.TSchoolRegistry;
import net.alshanex.tunes_n_tomes.registry.TSoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class HarmonicAriaSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "harmonic_aria");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(TSchoolRegistry.MELODY_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(15.0)
      .build();

   public HarmonicAriaSpell() {
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 8;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getTickDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.distance", new Object[]{Utils.stringTruncation(getRange(spellLevel), 1)})
      );
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
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
      return Optional.of((SoundEvent)TSoundRegistry.CRESCENDO.get());
   }

   public ICastDataSerializable getEmptyCastData() {
      return new HarmonicAriaCastData();
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
      if (!level.isClientSide) {
         AriaBeamEntity beam = new AriaBeamEntity(level, entity, this.castTime + 10);
         beam.setPos(entity.getX(), entity.getY(), entity.getZ());
         beam.setYRot(entity.getYRot());
         beam.setXRot(entity.getXRot());
         level.addFreshEntity(beam);
         if (playerMagicData != null) {
            HarmonicAriaCastData castData = new HarmonicAriaCastData();
            castData.setBeamEntityId(beam.getId());
            playerMagicData.setAdditionalCastData(castData);
         }
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
      if (playerMagicData != null && playerMagicData.getAdditionalCastData() instanceof HarmonicAriaCastData castData && entity instanceof Mob mob) {
         LivingEntity target = mob.getTarget();
         if (target != null) {
            castData.updateAim(target, 0.15F);
         }
      }

      Vec3 start = entity.getEyePosition();
      Vec3 end = start.add(entity.getForward().scale(getRange(spellLevel)));
      AABB boundingBox = entity.getBoundingBox().expandTowards(end.subtract(start));

      for (Entity target : level.getEntities(entity, boundingBox)) {
         HitResult hit = Utils.checkEntityIntersecting(target, start, end, 0.4F);
         if (hit.getType() != Type.MISS) {
            DamageSources.applyDamage(target, this.getTickDamage(spellLevel, entity), this.getDamageSource(entity));
            if (target instanceof LivingEntity livingEntity) {
               livingEntity.addEffect(new MobEffectInstance(TEffectRegistry.DEAFENED, 60));
            }
         }
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
      super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
      if (playerMagicData != null && playerMagicData.getAdditionalCastData() instanceof HarmonicAriaCastData castData) {
         int beamId = castData.getBeamEntityId();
         if (beamId != -1 && level.getEntity(beamId) instanceof AriaBeamEntity beam) {
            beam.discard();
         }
      }
   }

   public static float getRange(int level) {
      return 10.0F;
   }

   private float getTickDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.5F;
   }

   public boolean shouldAIStopCasting(int spellLevel, Mob mob, LivingEntity target) {
      return mob.distanceToSqr(target) > getRange(spellLevel) * getRange(spellLevel) * 1.2;
   }
}
