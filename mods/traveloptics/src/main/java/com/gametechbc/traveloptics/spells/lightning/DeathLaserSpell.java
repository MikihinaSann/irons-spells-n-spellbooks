package com.gametechbc.traveloptics.spells.lightning;

import com.gametechbc.traveloptics.entity.extended.ExtendedDeathLaserBeamEntity;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.init.TOEntities;
import com.gametechbc.traveloptics.init.TOItems;
import com.gametechbc.traveloptics.init.TOSounds;
import com.github.L_Ender.cataclysm.entity.projectile.Death_Laser_Beam_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;

@AutoSpellConfig
public class DeathLaserSpell extends AbstractSpell {
   private static final ResourceLocation SPELL_ID = new ResourceLocation("traveloptics", "death_laser");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.UNCOMMON)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(16.0)
      .build();

   public DeathLaserSpell() {
      super();
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 100;
   }

   public ResourceLocation getSpellResource() {
      return SPELL_ID;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)ModSounds.HARBINGER_DEATHLASER_PREPARE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)TOSounds.REFINED_DEATH_LASER_SHOOT.get());
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!level.isClientSide) {
         this.applyCasterEffects(caster);
         this.spawnDeathLaserBeamEntity(level, caster, spellLevel);
      }

      CameraShakeManager.addCameraShake(new CameraShakeData(25, caster.position(), 25.0F));
      super.onCast(level, spellLevel, caster, castSource, playerMagicData);
   }

   private void spawnDeathLaserBeamEntity(Level level, LivingEntity caster, int spellLevel) {
      float damage = this.getDamage(spellLevel, caster);
      double x = caster.getX();
      double y = caster.getY() + caster.getEyeHeight() - 0.2;
      double z = caster.getZ();
      float pitch = -((float)(caster.getXRot() * Math.PI / 180.0));
      float yaw = (float)((caster.getYRot() + 90.0F) * Math.PI / 180.0);
      ExtendedDeathLaserBeamEntity deathLaser = new ExtendedDeathLaserBeamEntity(
         (EntityType<? extends Death_Laser_Beam_Entity>)TOEntities.EXTENDED_DEATH_LASER_BEAM.get(), level, caster, x, y, z, yaw, pitch, 55, damage, 0.0F
      );
      level.addFreshEntity(deathLaser);
   }

   private void applyCasterEffects(LivingEntity caster) {
      if (CuriosApi.getCuriosInventory(caster)
         .map(curios -> !curios.findCurios(item -> item != null && item.is((Item)TOItems.ENERGY_UNBOUND_NECKLACE.get())).isEmpty())
         .orElse(false)) {
         caster.addEffect(new MobEffectInstance(TOEffects.CASTING_EFFECT, 75, 3, false, false, false));
      } else {
         caster.addEffect(new MobEffectInstance(TOEffects.FROZEN_SIGHT_EFFECT, 75, 1, false, false, false));
      }
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 5.0F;
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      float damage = this.getDamage(spellLevel, caster);
      return List.of(
         Component.translatable("ui.traveloptics.damage", new Object[]{Utils.stringTruncation(damage, 2)}), Component.literal("§9T.O Magic 'n Extras")
      );
   }

   public boolean stopSoundOnCancel() {
      return true;
   }
}
