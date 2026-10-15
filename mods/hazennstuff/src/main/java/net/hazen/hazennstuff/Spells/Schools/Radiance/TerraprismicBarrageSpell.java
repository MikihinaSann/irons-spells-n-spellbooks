package net.hazen.hazennstuff.Spells.Schools.Radiance;

import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.hazen.hazennstuff.Animations.HnSSpellAnimations;
import net.hazen.hazennstuff.Entity.Spells.Radiance.Terraprisma.Terraprisma;
import net.hazen.hazennstuff.Registries.HnSSchoolRegistry;
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

public class TerraprismicBarrageSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("hazennstuff", "terraprismic_barrage");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(HnSSchoolRegistry.RADIANCE_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(2.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 2)}));
   }

   public TerraprismicBarrageSpell() {
      this.manaCostPerLevel = 3;
      this.baseSpellPower = 12;
      this.spellPowerPerLevel = 1;
      this.castTime = 30;
      this.baseManaCost = 40;
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)HnSSounds.TERRAPRISMA_SUMMON.get());
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of((SoundEvent)HnSSounds.TERRAPRISMA_CASTING.get());
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public AnimationHolder getCastStartAnimation() {
      return HnSSpellAnimations.COMMAND_CHARGE;
   }

   public AnimationHolder getCastFinishAnimation() {
      return HnSSpellAnimations.COMMAND_CAST;
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public void onCast(Level world, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      if (!world.isClientSide) {
         int count = 2 + spellLevel;
         int delayBetween = 2;
         Vec3 look = caster.getLookAngle();
         Vec3 castPos = caster.position().add(0.0, caster.getEyeHeight() - 0.8, 0.0);

         for (int i = 0; i < count; i++) {
            Terraprisma terraprismaProjectile = new Terraprisma(world, caster);
            terraprismaProjectile.setDelay(i * delayBetween);
            terraprismaProjectile.setDamage(this.getDamage(spellLevel, caster));
            terraprismaProjectile.setSpawnPos(castPos);
            terraprismaProjectile.setCursorHoming(true);
            terraprismaProjectile.shoot(look);
            world.addFreshEntity(terraprismaProjectile);
         }

         super.onCast(world, spellLevel, caster, castSource, playerMagicData);
      }
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setIFrames(0);
   }

   private float getDamage(int spellLevel, LivingEntity entity) {
      return this.getSpellPower(spellLevel, entity) * 0.5F;
   }
}
