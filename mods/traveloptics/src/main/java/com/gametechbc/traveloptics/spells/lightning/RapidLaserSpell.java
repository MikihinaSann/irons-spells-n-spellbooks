package com.gametechbc.traveloptics.spells.lightning;

import com.github.L_Ender.cataclysm.entity.projectile.Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModEntities;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoSpellConfig
public class RapidLaserSpell extends AbstractSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "rapid_laser");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.COMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(12.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.direct_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public RapidLaserSpell() {
      super();
      this.manaCostPerLevel = 1;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 4;
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
      return Optional.of((SoundEvent)ModSounds.HARBINGER_LASER.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.spawnLaserBeam(level, entity, this.getDamage(spellLevel, entity), 1.5F);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnLaserBeam(Level level, LivingEntity caster, float damage, float speed) {
      Vec3 lookVec = caster.getViewVector(1.0F).normalize();
      Vec3 spreadVec = lookVec.add(0.0, 0.0, 0.0).normalize().scale(speed);
      Vec3 spawnPos = new Vec3(caster.getX(), caster.getY() + caster.getEyeHeight() * 0.8F, caster.getZ());
      float yRot = (float)(Mth.atan2(lookVec.z, lookVec.x) * (180.0 / Math.PI)) + 90.0F;
      float xRot = (float)(-(Mth.atan2(lookVec.y, Math.sqrt(lookVec.x * lookVec.x + lookVec.z * lookVec.z)) * (180.0 / Math.PI)));
      Laser_Beam_Entity laserBeam = new Laser_Beam_Entity(
         (EntityType)ModEntities.LASER_BEAM.get(), caster, spawnPos.x, spawnPos.y - 0.21, spawnPos.z, spreadVec, damage, level
      );
      laserBeam.setYRot(yRot);
      laserBeam.setXRot(xRot);
      level.addFreshEntity(laserBeam);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return 5.0F + this.getSpellPower(spellLevel, caster);
   }
}
