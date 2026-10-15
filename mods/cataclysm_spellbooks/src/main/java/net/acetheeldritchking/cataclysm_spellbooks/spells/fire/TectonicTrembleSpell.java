package net.acetheeldritchking.cataclysm_spellbooks.spells.fire;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import net.acetheeldritchking.cataclysm_spellbooks.registries.SpellRegistries;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TectonicTrembleSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "tectonic_tremble");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.LEGENDARY)
      .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
      .setMaxLevel(1)
      .setCooldownSeconds(50.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.range", new Object[]{Utils.stringTruncation(this.getRange(spellLevel, caster), 0)}),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)})
      );
   }

   public TectonicTrembleSpell() {
      this.manaCostPerLevel = 0;
      this.baseSpellPower = 15;
      this.spellPowerPerLevel = 0;
      this.castTime = 15;
      this.baseManaCost = 150;
   }

   public boolean canBeCraftedBy(Player player) {
      return false;
   }

   public boolean allowLooting() {
      return false;
   }

   public boolean allowCrafting() {
      return false;
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

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.STOMP;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      ScreenShake_Entity.ScreenShake(level, entity.position(), 10.0F, 0.15F, 20, 20);

      for (LivingEntity targets : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(this.getRange(spellLevel, entity)))) {
         boolean flag = DamageSources.applyDamage(targets, this.getDamage(spellLevel, entity), SpellRegistries.TECTONIC_TREMBLE.get().getDamageSource(entity));
         if (flag) {
            this.doKnockback(targets, entity, 2.0, 0.6);
         }

         targets.setRemainingFireTicks(10);
      }

      EarthquakeAoe aoe = new EarthquakeAoe(level);
      aoe.moveTo(entity.position());
      aoe.setOwner(entity);
      aoe.setCircular();
      aoe.setRadius(this.getRange(spellLevel, entity));
      aoe.setDuration(20);
      aoe.setDamage(0.0F);
      aoe.setSlownessAmplifier(0);
      level.addFreshEntity(aoe);
      CSUtils.spawnFlameJetWindmill(5, 3, 1.0, 1.25, 0.0, 1, entity, level, 1.0F);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.FIRE.get()).getTargetingColor(), this.getRange(spellLevel, entity)),
         entity.getX(),
         entity.getY() + 0.5,
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) * 0.9F;
   }

   private int getRange(int spellLevel, LivingEntity caster) {
      return (int)(5.0F + spellLevel * this.getEntityPowerMultiplier(caster));
   }

   private void doKnockback(LivingEntity target, LivingEntity caster, double x, double y) {
      double diffX = target.getX() - caster.getX();
      double diffZ = target.getZ() - caster.getZ();
      double power = Math.max(diffX * diffX + diffZ * diffZ, 0.001);
      target.push(diffX / diffZ * x, y, diffZ / power * x);
   }
}
