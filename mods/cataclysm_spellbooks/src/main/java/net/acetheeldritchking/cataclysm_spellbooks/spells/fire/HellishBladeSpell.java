package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.init.ModItems;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.hellish_blade.HellishBladeProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class HellishBladeSpell extends AbstractIgnisSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "hellish_blade");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(90.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.cataclysm_spellbooks.incinerator_damage", new Object[]{Utils.stringTruncation(this.getBonusDamage(spellLevel, caster), 2)})
      );
   }

   public HellishBladeSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 2;
      this.castTime = 20;
      this.baseManaCost = 150;
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
      return Optional.of((SoundEvent)SoundRegistry.FIREBALL_START.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.IGNIS_ARMOR_BREAK.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, 0.15F);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetEntityCastData) {
         LivingEntity targetEntity = targetEntityCastData.getTarget((ServerLevel)level);
         if (targetEntity != null) {
            double targetEye = targetEntity.getEyeHeight();
            float MAX_HEALTH = entity.getMaxHealth();
            float baseHealth = entity.getHealth();
            double percent = baseHealth / MAX_HEALTH * 100.0F;
            Item incinerator = (Item)ModItems.THE_INCINERATOR.get();
            Vec3 center = targetEntity.position().add(0.0, targetEye / 2.0, 0.0);
            Vec3 spawn = center.add(0.0, 10.0, 0.0);
            Vec3 motion = center.subtract(spawn).normalize();
            HellishBladeProjectile hellishBlade = new HellishBladeProjectile(level, entity);
            hellishBlade.moveTo(spawn);
            hellishBlade.shoot(motion);
            float damage = hellishBlade.getIsSoul() ? this.getDamage(spellLevel, entity) * 1.5F : this.getDamage(spellLevel, entity);
            float bonusDamage = hellishBlade.getIsSoul() ? this.getBonusDamage(spellLevel, entity) * 1.5F : this.getBonusDamage(spellLevel, entity);
            if (percent <= 50.0) {
               hellishBlade.setIsSoul(true);
            } else {
               hellishBlade.setIsSoul(false);
            }

            if (entity.getMainHandItem().is(incinerator)) {
               hellishBlade.setDamage(bonusDamage);
            } else {
               hellishBlade.setDamage(damage);
            }

            level.addFreshEntity(hellishBlade);
         }
      }

      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 2.0F;
   }

   private float getBonusDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getDamage(spellLevel, caster);
      int bonusAmount = (int)(1.5 + spellLevel);
      return baseDamage + bonusAmount;
   }
}
