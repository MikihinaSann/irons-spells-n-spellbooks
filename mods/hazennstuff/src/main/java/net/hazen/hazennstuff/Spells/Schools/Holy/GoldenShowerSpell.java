package net.hazen.hazennstuff.Spells.Schools.Holy;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Holy.IchorStream.IchorStream;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.hazen.hazennstuff.Spells.AbstractSpells.AbstractGoldenShowerSpell;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GoldenShowerSpell extends AbstractGoldenShowerSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "golden_shower");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(3.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public GoldenShowerSpell() {
      this.manaCostPerLevel = 6;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 1;
      this.castTime = 55;
      this.baseManaCost = 30;
   }

   public int getCastTime(int spellLevel) {
      return this.castTime + 5 * spellLevel;
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

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.empty();
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)HnSSounds.GOLDEN_SHOWER_CAST.get());
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (playerMagicData != null && (playerMagicData.getCastDurationRemaining() + 1) % 5 == 0) {
         this.shootIchorStream(level, spellLevel, entity);
      }
   }

   public void shootIchorStream(Level world, int spellLevel, LivingEntity entity) {
      Vec3 origin = entity.getEyePosition().add(entity.getForward().normalize().scale(0.2));
      IchorStream ichorStream = new IchorStream(world, entity, spellLevel, this.getSpellPower(spellLevel, entity));
      ichorStream.setPos(origin.subtract(0.0, ichorStream.getBbHeight(), 0.0));
      world.addFreshEntity(ichorStream);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.4F;
   }
}
