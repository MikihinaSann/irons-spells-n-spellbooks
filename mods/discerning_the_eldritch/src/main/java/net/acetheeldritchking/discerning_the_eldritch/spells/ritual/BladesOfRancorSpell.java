package net.acetheeldritchking.discerning_the_eldritch.spells.ritual;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.discerning_the_eldritch.entity.spells.blade_of_rancor.BladeOfRancorProjectile;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESchoolRegistry;
import net.acetheeldritchking.discerning_the_eldritch.registries.DTESoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BladesOfRancorSpell extends AbstractRitualSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("discerning_the_eldritch", "blades_of_rancor");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(DTESchoolRegistry.RITUAL_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster)}),
         Component.translatable("ui.discerning_the_eldritch.extra_damage")
      );
   }

   public BladesOfRancorSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 5;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 90;
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

   @Override
   public boolean isComplex() {
      return false;
   }

   @Override
   public boolean isSuperComplex() {
      return false;
   }

   public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
      return spellLevel;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)DTESoundRegistry.MAGIC_SWORD_CAST.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      PlayerRecasts recasts = playerMagicData.getPlayerRecasts();
      if (!recasts.hasRecastForSpell(this.getSpellId())) {
         playerMagicData.getPlayerRecasts()
            .addRecast(new RecastInstance(this.getSpellId(), spellLevel, this.getRecastCount(spellLevel, entity), 160, castSource, null), playerMagicData);
      }

      BladeOfRancorProjectile bladeOfRancor = new BladeOfRancorProjectile(level, entity);
      Vec3 origin = entity.getEyePosition().add(entity.getForward().normalize().scale(0.2F)).subtract(0.0, 0.15, 0.0);
      bladeOfRancor.setPos(origin.subtract(0.0, bladeOfRancor.getBbHeight(), 0.0));
      Vec3 vec3 = entity.getForward().add(0.0, 0.2, 0.0).normalize();
      bladeOfRancor.shoot(vec3.scale(0.5), 0.4F);
      bladeOfRancor.setDamage(this.getDamage(spellLevel, entity));
      bladeOfRancor.setCursorHoming(true);
      bladeOfRancor.setNoGravity(true);
      level.addFreshEntity(bladeOfRancor);
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
