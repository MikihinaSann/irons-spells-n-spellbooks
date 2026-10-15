package net.acetheeldritchking.cataclysm_spellbooks.spells.ice;

import com.github.L_Ender.cataclysm.entity.effect.ScreenShake_Entity;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.spells.EarthquakeAoe;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import java.util.List;
import java.util.Optional;
import net.acetheeldritchking.aces_spell_utils.spells.ASSpellAnimations;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.cataclysm_spellbooks.spells.CSSpellAnimations;
import net.acetheeldritchking.cataclysm_spellbooks.util.CSUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MalevolentBattlefieldSpell extends AbstractMaledictusSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("cataclysm_spellbooks", "malevolent_battlefield");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
      .setMaxLevel(10)
      .setCooldownSeconds(60.0)
      .build();

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.cataclysm_spellbooks.halberd_rings", new Object[]{spellLevel}),
         Component.translatable(
            "ui.cataclysm_spellbooks.halberd_amount", new Object[]{Utils.stringTruncation(this.getHalberdsPerBranch(spellLevel, caster), 0)}
         ),
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{Utils.stringTruncation(this.getDamage(spellLevel, caster), 1)}),
         Component.translatable(
            "ui.cataclysm_spellbooks.maledictus_armory_bonus", new Object[]{Utils.stringTruncation(this.getBonusDamage(spellLevel, caster), 1)}
         )
      );
   }

   public MalevolentBattlefieldSpell() {
      this.manaCostPerLevel = 20;
      this.baseSpellPower = 10;
      this.spellPowerPerLevel = 3;
      this.castTime = 80;
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

   public AnimationHolder getCastStartAnimation() {
      return CSSpellAnimations.ANIMATION_MALEVOLENT_HAND_SIGN;
   }

   public AnimationHolder getCastFinishAnimation() {
      return ASSpellAnimations.ANIMATION_MALEVOLENT_HAND_SIGN_RELEASE;
   }

   public Optional<SoundEvent> getCastStartSound() {
      return Optional.of(SoundEvents.WARDEN_HEARTBEAT);
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)ModSounds.MALEDICTUS_BATTLE_CRY.get());
   }

   public boolean canBeInterrupted(@Nullable Player player) {
      return false;
   }

   public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      double radius = 15.0;

      for (LivingEntity targets : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius))) {
         targets.addEffect(new MobEffectInstance(MobEffects.DARKNESS, this.getCastTime(spellLevel), 1, false, false, false));
      }

      entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, this.getCastTime(spellLevel), 1, false, false, false));
      super.onServerPreCast(level, spellLevel, entity, playerMagicData);
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      if (entity.tickCount % 10 == 0) {
         ASUtils.spawnParticlesInRing(16, 1.25F, 3.25F, 5.25F, 0.5F, 0.1F, entity, (ParticleOptions)ModParticle.PHANTOM_WING_FLAME.get());
      }

      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      Item soulRenderer = (Item)ModItems.SOUL_RENDER.get();
      Item annihilator = (Item)ModItems.THE_ANNIHILATOR.get();
      if (!entity.getMainHandItem().is(soulRenderer) && !entity.getMainHandItem().is(annihilator)) {
         CSUtils.spawnHalberdWindmill(
            spellLevel * 4, this.getHalberdsPerBranch(spellLevel, entity), 1.5, 0.75, 0.2, 1, entity, level, this.getDamage(spellLevel, entity), spellLevel
         );
      } else {
         CSUtils.spawnHalberdWindmill(
            spellLevel * 4,
            this.getHalberdsPerBranch(spellLevel, entity),
            1.5,
            0.75,
            0.2,
            1,
            entity,
            level,
            this.getBonusDamage(spellLevel, entity),
            spellLevel
         );
      }

      EarthquakeAoe aoe = new EarthquakeAoe(level);
      aoe.moveTo(entity.position());
      aoe.setOwner(entity);
      aoe.setCircular();
      aoe.setRadius(6.0F);
      aoe.setDuration(20);
      aoe.setDamage(0.0F);
      aoe.setSlownessAmplifier(0);
      MagicManager.spawnParticles(
         level,
         new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ICE.get()).getTargetingColor(), 4.0F),
         entity.getX(),
         entity.getY() + 0.8F,
         entity.getZ(),
         1,
         0.0,
         0.0,
         0.0,
         0.0,
         true
      );
      ScreenShake_Entity.ScreenShake(level, entity.position(), 6.0F, 0.15F, 20, 20);
      level.addFreshEntity(aoe);
      super.onCast(level, spellLevel, entity, castSource, playerMagicData);
   }

   private int getHalberdsPerBranch(int spellLevel, LivingEntity caster) {
      return (int)Mth.clamp(this.getSpellPower(spellLevel, caster), 1.0F, 5.0F);
   }

   private float getDamage(int spellLevel, LivingEntity caster) {
      return this.getSpellPower(spellLevel, caster) + 7.0F;
   }

   private float getBonusDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getDamage(spellLevel, caster);
      int bonusAmount = (int)(3.5 + spellLevel);
      return baseDamage + bonusAmount;
   }
}
