package com.gametechbc.traveloptics.spells.holy;

import com.gametechbc.traveloptics.api.particle.CylinderParticleManager;
import com.gametechbc.traveloptics.api.particle.ParticleDirection;
import com.gametechbc.traveloptics.api.spells.AbstractUniqueSpell;
import com.gametechbc.traveloptics.api.utils.SummonCheckHelper;
import com.gametechbc.traveloptics.config.SpellsConfig;
import com.gametechbc.traveloptics.entity.summons.SummonedKoboleton;
import com.gametechbc.traveloptics.entity.summons.SummonedWadjet;
import com.gametechbc.traveloptics.init.TOEffects;
import com.gametechbc.traveloptics.util.SummonTypes;
import com.github.L_Ender.cataclysm.init.ModItems;
import com.github.L_Ender.cataclysm.init.ModParticle;
import com.github.L_Ender.cataclysm.init.ModSounds;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AutoSpellConfig;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class SummonDesertDwellers extends AbstractUniqueSpell {
   private final ResourceLocation spellId = new ResourceLocation("traveloptics", "summon_desert_dwellers");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.RARE)
      .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(420.0)
      .build();

   public SummonDesertDwellers() {
      super();
      this.manaCostPerLevel = 75;
      this.baseSpellPower = 1;
      this.spellPowerPerLevel = 1;
      this.castTime = 60;
      this.baseManaCost = 100;
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
      return Optional.of((SoundEvent)ModSounds.REMNANT_ROAR.get());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
      if (entity instanceof Player player
         && (Boolean)SpellsConfig.limitGroupSummons.get()
         && SummonCheckHelper.hasActiveSummons(player, 128.0, SummonTypes.getGroupSummons())) {
         player.displayClientMessage(Component.translatable("spell.traveloptics.summon_group.warning").withStyle(ChatFormatting.RED), true);
         return false;
      } else {
         return true;
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
      CylinderParticleManager.spawnParticles(level, entity, 2, (ParticleOptions)ModParticle.SANDSTORM.get(), ParticleDirection.UPWARD, 2.0, 2.0, -1.0);
      super.onServerCastTick(level, spellLevel, entity, playerMagicData);
   }

   public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
      int summonTime = 12000;
      int koboletonCount = (int)this.getKoboletonCount(spellLevel);
      double radius = 3.5;
      double angleIncrement = (Math.PI * 2) / koboletonCount;

      for (int i = 0; i < koboletonCount; i++) {
         double angle = i * angleIncrement;
         double xOffset = radius * Math.cos(angle);
         double zOffset = radius * Math.sin(angle);
         SummonedKoboleton koboleton = new SummonedKoboleton(world, entity);
         koboleton.setPos(entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset);
         koboleton.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getKoboletonDamage(spellLevel, entity));
         koboleton.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getKoboletonHealth(spellLevel));
         koboleton.setHealth(koboleton.getMaxHealth());
         this.equip(koboleton);
         world.addFreshEntity(koboleton);
         koboleton.addEffect(new MobEffectInstance(TOEffects.DESERT_DWELLER_TIMER, summonTime, 0, false, false, false));
         MagicManager.spawnParticles(
            world,
            (ParticleOptions)ModParticle.SANDSTORM.get(),
            entity.getX() + xOffset,
            entity.getY() + 1.5,
            entity.getZ() + zOffset,
            20,
            0.0,
            0.0,
            0.0,
            0.03,
            false
         );
      }

      SummonedWadjet wadjet = new SummonedWadjet(world, entity);
      wadjet.setPos(entity.position());
      wadjet.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(this.getWadjetDamage(spellLevel, entity));
      wadjet.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(this.getWadjetHealth(spellLevel));
      wadjet.setHealth(wadjet.getMaxHealth());
      world.addFreshEntity(wadjet);
      wadjet.addEffect(new MobEffectInstance(TOEffects.DESERT_DWELLER_TIMER, summonTime, 0, false, false, false));
      MagicManager.spawnParticles(
         world, (ParticleOptions)ModParticle.SANDSTORM.get(), entity.getX(), entity.getY() + 2.5, entity.getZ(), 40, 0.0, 0.0, 0.0, 0.03, false
      );
      int effectAmplifier = 0;
      if (entity.hasEffect(TOEffects.DESERT_DWELLER_TIMER)) {
         effectAmplifier += entity.getEffect(TOEffects.DESERT_DWELLER_TIMER).getAmplifier() + 1;
      }

      entity.addEffect(new MobEffectInstance(TOEffects.DESERT_DWELLER_TIMER, summonTime, effectAmplifier, false, false, true));
      CameraShakeManager.addCameraShake(new CameraShakeData(35, entity.position(), 25.0F));
      super.onCast(world, spellLevel, entity, castSource, playerMagicData);
   }

   private float getKoboletonCount(int spellLevel) {
      return spellLevel;
   }

   private float getKoboletonHealth(int spellLevel) {
      return spellLevel * 5.0F;
   }

   private float getKoboletonDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = this.getSpellPower(spellLevel, caster) * 0.6F;
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getKoboletonDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = this.getSpellPower(spellLevel, caster) * 0.6F;
         double summonedDamageMultiplier = caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)
            ? caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE)
            : 1.0;
         float finalDamage = (float)(baseDamage * summonedDamageMultiplier);
         String baseDamageText = Utils.stringTruncation(baseDamage, 1);
         String finalDamageText = Utils.stringTruncation(finalDamage, 1);
         return summonedDamageMultiplier > 1.0 ? String.format("%s -> %s", baseDamageText, finalDamageText) : baseDamageText;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   private float getWadjetHealth(int spellLevel) {
      return 50.0F + spellLevel * 20.0F;
   }

   private float getWadjetDamage(int spellLevel, LivingEntity caster) {
      float baseDamage = 2.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
      double summonedDamageMultiplier = 1.0;
      if (caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)) {
         summonedDamageMultiplier = caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE);
      }

      return (float)(baseDamage * summonedDamageMultiplier);
   }

   private String getWadjetDamageText(int spellLevel, LivingEntity caster) {
      if (caster != null) {
         float baseDamage = 2.0F + this.getSpellPower(spellLevel, caster) * 2.0F;
         double summonedDamageMultiplier = caster.getAttributes().hasAttribute(AttributeRegistry.SUMMON_DAMAGE)
            ? caster.getAttributeValue(AttributeRegistry.SUMMON_DAMAGE)
            : 1.0;
         float finalDamage = (float)(baseDamage * summonedDamageMultiplier);
         String baseDamageText = Utils.stringTruncation(baseDamage, 1);
         String finalDamageText = Utils.stringTruncation(finalDamage, 1);
         return summonedDamageMultiplier > 1.0 ? String.format("%s -> %s", baseDamageText, finalDamageText) : baseDamageText;
      } else {
         return this.getSpellPower(spellLevel, caster) + "";
      }
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      return List.of(
         Component.translatable("ui.traveloptics.summon_count", new Object[]{this.getKoboletonCount(spellLevel) + 1.0F}),
         Component.translatable("ui.traveloptics.koboleton_hp", new Object[]{this.getKoboletonHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.koboleton_damage", new Object[]{this.getKoboletonDamageText(spellLevel, caster)}),
         Component.translatable("ui.traveloptics.wadjet_hp", new Object[]{this.getWadjetHealth(spellLevel)}),
         Component.translatable("ui.traveloptics.wadjet_damage", new Object[]{this.getWadjetDamageText(spellLevel, caster)}),
         Component.literal("§9T.O Magic 'n Extras")
      );
   }

   private void equip(Mob mob) {
      mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)ModItems.KHOPESH.get()));
      mob.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }
}
