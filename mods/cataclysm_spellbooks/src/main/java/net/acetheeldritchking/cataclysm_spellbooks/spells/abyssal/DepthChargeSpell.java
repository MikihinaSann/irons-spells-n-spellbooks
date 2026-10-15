package net.acetheeldritchking.cataclysm_spellbooks.spells.abyssal;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.The_Leviathan.Abyss_Mine_Entity;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.registries.CSSchoolRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DepthChargeSpell extends AbstractAbyssalSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "depth_charge");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(CSSchoolRegistry.ABYSSAL_RESOURCE)
      .setMaxLevel(3)
      .setCooldownSeconds(40.0)
      .build();

   public DepthChargeSpell() {
      this.manaCostPerLevel = 50;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 10;
      this.castTime = 10;
      this.baseManaCost = 50;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.LEVIATHAN_STUN_ROAR.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      double casterX = entity.getX();
      double casterY = entity.getY();
      double casterZ = entity.getZ();
      double radiusX = casterX + spellLevel;
      double radiusZ = casterZ + spellLevel;
      float f = (float)Mth.atan2(radiusZ - casterZ, radiusX - casterX);

      for (int l = 0; l < 15; l++) {
         int j = (int)(2.0F * l);

         for (int i = 0; i < this.amountForMines(spellLevel, entity); i++) {
            double nearbyRandomX = casterX + entity.getRandom().nextGaussian() * this.randomNearby1(spellLevel);
            double nearbyRandomY = casterY + entity.getRandom().nextGaussian() * this.randomNearby2(spellLevel);
            double nearbyRandomZ = casterZ + entity.getRandom().nextGaussian() * this.randomNearby1(spellLevel);
            this.spawnMines(nearbyRandomX, nearbyRandomY, nearbyRandomZ, f, j, entity);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void spawnMines(double x, double y, double z, float rotation, int delay, LivingEntity caster) {
      Level level = caster.level();
      Abyss_Mine_Entity abyssMine = new Abyss_Mine_Entity(level, x, y, z, rotation, delay, caster);
      if (caster != null && abyssMine.level().noCollision(abyssMine)) {
         caster.level().addFreshEntity(abyssMine);
      }
   }

   private double randomNearby1(int spellLevel) {
      return 4.0 * spellLevel;
   }

   private double randomNearby2(int spellLevel) {
      return 3.0 * spellLevel;
   }

   private int amountForMines(int spellLevel, LivingEntity caster) {
      return (int)Mth.clamp(this.getSpellPower(spellLevel, caster), 0.0F, 5.0F);
   }
}
