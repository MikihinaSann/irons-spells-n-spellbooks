package net.hazen.hazennstuff.Spells.Schools.Ice;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.hazen.hazennstuff.Entity.Spells.Ice.IceArrow.IceArrow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class IceArrowSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "ice_arrow");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(8.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{Utils.stringTruncation(this.getRadius(spellLevel, caster), 1)}),
         Component.translatable("ui.irons_spellbooks.aoe_damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster) * 0.5, 1)})
      );
   }

   public IceArrowSpell() {
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 11;
      this.spellPowerPerLevel = 1;
      this.castTime = 20;
      this.baseManaCost = 40;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)SoundRegistry.MAGIC_ARROW_CHARGE.get());
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundRegistry.MAGIC_ARROW_RELEASE.get());
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      IceArrow magicArrow = new IceArrow(level, entity);
      magicArrow.setPos(entity.position().add(0.0, entity.getEyeHeight() - magicArrow.getBoundingBox().getYsize() * 0.5, 0.0).add(entity.getForward()));
      magicArrow.shoot(entity.getLookAngle());
      magicArrow.setDamage(this.getDamage(spellLevel, entity));
      magicArrow.setExplosionRadius(this.getRadius(spellLevel, entity));
      level.addFreshEntity(magicArrow);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster);
   }

   public float getRadius(int spellLevel, LivingEntity caster) {
      return 3.0F;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.BOW_CHARGE_ANIMATION;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.none();
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setFreezeTicks(80);
   }
}
