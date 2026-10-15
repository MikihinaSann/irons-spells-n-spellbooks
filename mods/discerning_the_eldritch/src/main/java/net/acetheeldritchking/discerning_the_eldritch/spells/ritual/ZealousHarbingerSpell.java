package net.acetheeldritchking.discerning_the_eldritch.spells.ritual;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.cataclysm_blade_projectile.CataclysmBladeBigProjectile;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ZealousHarbingerSpell extends AbstractRitualSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "zealous_harbinger");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(DTESchoolRegistry.RITUAL_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(35.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.translatable("ui.discerning_the_eldritch.extra_damage")
      );
   }

   public ZealousHarbingerSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 25;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
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

   @Override
   public boolean isComplex() {
      return true;
   }

   @Override
   public boolean isSuperComplex() {
      return false;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.DARK_MAGIC_CHARGE_1.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.DARK_MAGIC_SWORD_CAST.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 15 + spellLevel, 0.15F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
         LivingEntity targetEntity = targetEntityCastData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            Vec3 lookDir = entity.getLookAngle();
            Vec3 flatDir = new Vec3(lookDir.x, 1.55, lookDir.z).normalize();
            double offset = -2.0;
            Vec3 spawnPos = entity.position().subtract(flatDir.scale(offset));

            for (int i = 0; i < 6; i++) {
               CataclysmBladeBigProjectile cataclysmBlade = new CataclysmBladeBigProjectile(level, entity);
               cataclysmBlade.setPos(spawnPos.x, spawnPos.y, spawnPos.z + i);
               cataclysmBlade.setPos(spawnPos.x, spawnPos.y, spawnPos.z - i);
               Vec3 vec3 = targetEntity.getForward().add(0.0, 0.05, 0.0).normalize();
               cataclysmBlade.shoot(vec3.scale(0.5), 0.4F);
               cataclysmBlade.setDamage(this.getDamage(spellLevel, entity));
               cataclysmBlade.setHomingTarget(targetEntity);
               level.addFreshEntity(cataclysmBlade);
            }
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return (float)(this.getSpellPower(spellLevel, caster) * 0.45 + this.getWeaponDamage(caster));
   }

   private float getWeaponDamage(LivingEntity caster) {
      return Utils.getWeaponDamage(caster);
   }

   private String getDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float weaponDamage = Utils.getWeaponDamage(caster);
         String plus = "";
         if (weaponDamage > 0.0F) {
            plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
         }

         String damage = Utils.stringTruncation(this.getDamage(spellLevel, caster), 1);
         return damage + plus;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   public ICastDataSerializable getEmptyCastData() {
      return new MultiTargetEntityCastData(new Entity[0]);
   }
}
