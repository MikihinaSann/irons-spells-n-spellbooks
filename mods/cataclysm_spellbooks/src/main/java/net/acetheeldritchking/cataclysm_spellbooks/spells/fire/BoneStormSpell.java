package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.entity.projectile.Blazing_Bone_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BoneStormSpell extends AbstractIgnisSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "bone_storm");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(80.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.bone_speed_1", new Object[]{Utils.stringTruncation(this.getBoneSpeed(0.2F, spellLevel), 2)}),
         Component.translatable("ui.cataclysm_spellbooks.bone_speed_2", new Object[]{Utils.stringTruncation(this.getBoneSpeed(0.3F, spellLevel), 2)}),
         Component.translatable("ui.cataclysm_spellbooks.bone_speed_3", new Object[]{Utils.stringTruncation(this.getBoneSpeed(0.1F, spellLevel), 2)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel), 2)})
      );
   }

   public BoneStormSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 0;
      this.spellPowerPerLevel = 1;
      this.castTime = 100;
      this.baseManaCost = 15;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public CastType getCastType() {
      return CastType.CONTINUOUS;
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      this.launchBone1(entity, spellLevel, level);
      this.launchBone2(entity, spellLevel, level);
      this.launchBone3(entity, spellLevel, level);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private void launchBone1(LivingEntity caster, int spellLevel, Level level) {
      caster.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.75F);

      for (int i = 0; i < 8; i++) {
         float throwAngle = (float)(i * Math.PI / 4.0);
         double casterX = caster.getX() + Mth.cos(throwAngle);
         double casterY = caster.getY() + caster.getBbHeight() * 0.62;
         double casterZ = caster.getZ() + Mth.sin(throwAngle);
         double angleX = Mth.cos(throwAngle);
         double angleY = 0.2;
         double angleZ = Mth.sin(throwAngle);
         Blazing_Bone_Entity blazingBone = new Blazing_Bone_Entity(level, this.getDamage(spellLevel), caster);
         blazingBone.moveTo(casterX, casterY, casterZ, i * 45.0F, caster.getXRot());
         float speed = 0.3F;
         float speedSpellLevel = this.getBoneSpeed(speed, spellLevel);
         blazingBone.setNoGravity(true);
         blazingBone.shoot(angleX, angleY, angleZ, speedSpellLevel, 1.0F);
         level.addFreshEntity(blazingBone);
      }
   }

   private void launchBone2(LivingEntity caster, int spellLevel, Level level) {
      caster.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.75F);

      for (int i = 0; i < 6; i++) {
         float throwAngle = (float)(i * Math.PI / 3.0);
         double casterX = caster.getX() + Mth.cos(throwAngle);
         double casterY = caster.getY() + caster.getBbHeight() * 0.62;
         double casterZ = caster.getZ() + Mth.sin(throwAngle);
         double angleX = Mth.cos(throwAngle);
         double angleY = 0.1;
         double angleZ = Mth.sin(throwAngle);
         Blazing_Bone_Entity blazingBone = new Blazing_Bone_Entity(level, this.getDamage(spellLevel), caster);
         blazingBone.moveTo(casterX, casterY, casterZ, i * 60.0F, caster.getXRot());
         float speed = 0.4F;
         float speedSpellLevel = this.getBoneSpeed(speed, spellLevel);
         blazingBone.setNoGravity(true);
         blazingBone.shoot(angleX, angleY, angleZ, speedSpellLevel, 1.0F);
         level.addFreshEntity(blazingBone);
      }
   }

   private void launchBone3(LivingEntity caster, int spellLevel, Level level) {
      caster.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.75F);

      for (int i = 0; i < 10; i++) {
         float throwAngle = (float)(i * Math.PI / 5.0);
         double casterX = caster.getX() + Mth.cos(throwAngle);
         double casterY = caster.getY() + caster.getBbHeight() * 0.62;
         double casterZ = caster.getZ() + Mth.sin(throwAngle);
         double angleX = Mth.cos(throwAngle);
         double angleY = 0.1;
         double angleZ = Mth.sin(throwAngle);
         Blazing_Bone_Entity blazingBone = new Blazing_Bone_Entity(level, this.getDamage(spellLevel), caster);
         blazingBone.moveTo(casterX, casterY, casterZ, i * 36.0F, caster.getXRot());
         float speed = 0.2F;
         float speedSpellLevel = this.getBoneSpeed(speed, spellLevel);
         blazingBone.setNoGravity(true);
         blazingBone.shoot(angleX, angleY, angleZ, speedSpellLevel, 1.0F);
         level.addFreshEntity(blazingBone);
      }
   }

   private float getBoneSpeed(float speed, int spellLevel) {
      return speed * spellLevel;
   }

   private float getDamage(int spellLevel) {
      return (float)(spellLevel * 1.5);
   }
}
