package net.hazen.hazennstuff.Spells.Schools.Lightning;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Entity.Spells.Lightning.EnergyBurst.EnergyBurst;
import net.hazen.hazennstuff.Registries.HnSSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EnergyBurstSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "energy_burst");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.LIGHTNING_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(6.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public EnergyBurstSpell() {
      this.manaCostPerLevel = 6;
      this.baseSpellPower = 20;
      this.spellPowerPerLevel = 1;
      this.castTime = 0;
      this.baseManaCost = 30;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)HnSSounds.SPARK_CAST.value());
   }

   public CastType getCastType() {
      return CastType.INSTANT;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public void onCast(Level world, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!world.isClientSide) {
         int count = 3;
         int delayBetween = 3;
         Vec3 look = caster.getLookAngle();
         Vec3 castPos = caster.position().add(0.0, caster.getEyeHeight() - 0.8, 0.0);

         for (int i = 0; i < count; i++) {
            EnergyBurst burst = new EnergyBurst(world, caster);
            burst.setDelay(i * delayBetween);
            burst.setDamage(this.getDamage(spellLevel, caster));
            burst.setSpawnPos(castPos);
            burst.shoot(look);
            world.addFreshEntity(burst);
         }

         super.onCast(world, spellLevel, caster, castSource, playerMagicData);
      }
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.3F;
   }
}
