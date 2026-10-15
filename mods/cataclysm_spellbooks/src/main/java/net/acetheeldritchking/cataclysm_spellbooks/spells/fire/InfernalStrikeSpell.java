package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.init.ModItems;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.cataclysm_spellbooks.entity.spells.infernal_blade.InfernalBladeProjectile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class InfernalStrikeSpell extends AbstractIgnisSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "infernal_strike");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(8)
      .setCooldownSeconds(20.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.cataclsym_spellboks.incinerator_damage", new Object[]{Utils.stringTruncation(this.getBonusDamage(spellLevel, caster), 2)})
      );
   }

   public InfernalStrikeSpell() {
      this.manaCostPerLevel = 10;
      this.baseSpellPower = 2;
      this.spellPowerPerLevel = 2;
      this.castTime = 0;
      this.baseManaCost = 110;
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
      return Optional.of((SoundEvent)SoundRegistry.FLAMING_STRIKE_SWING.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      InfernalBladeProjectile infernalBlade = new InfernalBladeProjectile(level, entity);
      infernalBlade.setPos(entity.position().add(0.0, entity.getEyeHeight() - infernalBlade.getBoundingBox().getYsize() * 0.5, 0.0));
      infernalBlade.shootFromRotation(entity, entity.getXRot(), entity.getYHeadRot(), 0.0F, 1.0F, 1.0F);
      Item incinerator = (Item)ModItems.THE_INCINERATOR.get();
      float MAX_HEALTH = entity.getMaxHealth();
      float baseHealth = entity.getHealth();
      double percent = baseHealth / MAX_HEALTH * 100.0F;
      float damage = infernalBlade.getIsSoul() ? this.getDamage(spellLevel, entity) * 1.5F : this.getDamage(spellLevel, entity);
      float bonusDamage = infernalBlade.getIsSoul() ? this.getBonusDamage(spellLevel, entity) * 1.5F : this.getBonusDamage(spellLevel, entity);
      if (entity.getMainHandItem().is(incinerator)) {
         infernalBlade.setDamage(bonusDamage);
      } else {
         infernalBlade.setDamage(damage);
      }

      if (percent <= 50.0) {
         infernalBlade.setIsSoul(true);
      } else {
         infernalBlade.setIsSoul(false);
      }

      level.addFreshEntity(infernalBlade);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public SpellDamageSource getDamageSource(Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 1.5F;
   }

   private float getBonusDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getDamage(spellLevel, caster);
      int bonusAmount = (int)(1.5 + spellLevel);
      return baseDamage + bonusAmount;
   }
}
