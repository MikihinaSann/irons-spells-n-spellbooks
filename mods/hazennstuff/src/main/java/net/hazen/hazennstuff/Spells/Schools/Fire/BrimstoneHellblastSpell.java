package net.hazen.hazennstuff.Spells.Schools.Fire;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Fire.BrimstoneHellblast.BrimstoneHellblast;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.AbstractSpells.AbstractCalamitasSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BrimstoneHellblastSpell extends AbstractCalamitasSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "brimstone_hellblast");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(25.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}),
         Component.translatable("ui.irons_spellbooks.radius", new Object[]{this.getRadius(spellLevel, caster)})
      );
   }

   @Override
   public boolean allowLooting() {
      return false;
   }

   public BrimstoneHellblastSpell() {
      this.manaCostPerLevel = 15;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
      this.baseManaCost = 60;
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
      return Optional.of((SoundEvent)HnSSounds.BRIMSTONE_CAST.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Vec3 origin = entity.getEyePosition();
      BrimstoneHellblast brimstoneHellblast = new BrimstoneHellblast(world, entity);
      brimstoneHellblast.setDamage(this.getDamage(spellLevel, entity));
      brimstoneHellblast.setExplosionRadius(this.getRadius(spellLevel, entity));
      brimstoneHellblast.setPos(origin.add(entity.getForward()).subtract(0.0, brimstoneHellblast.getBbHeight() / 2.0F, 0.0));
      brimstoneHellblast.shoot(entity.getLookAngle());
      world.addFreshEntity(brimstoneHellblast);
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public float getDamage(int spellLevel, LivingEntity caster) {
      if (caster == null) {
         return this.getSpellPower(spellLevel, null) * 7.0F;
      } else {
         double firePower = caster.getAttributeValue(AttributeRegistry.FIRE_SPELL_POWER);
         double bloodPower = caster.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER);
         return (float)(5.0 + 5.0 * this.getSpellPower(spellLevel, caster) * (0.5 * firePower + 0.5 * bloodPower));
      }
   }

   public int getRadius(int spellLevel, LivingEntity caster) {
      return 2 + (int)this.getSpellPower(spellLevel, caster);
   }
}
