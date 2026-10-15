package com.somake.somakespells.spells.blood;

import com.somake.somakespells.entity.BloodboundBladeProjectileEntity;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BloodboundBladeSpell extends AbstractSpell {
   private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath("somakespells", "bloodbound_blade");
   private final DefaultConfig defaultConfig = new DefaultConfig()
      .setMinRarity(SpellRarity.EPIC)
      .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
      .setMaxLevel(5)
      .setCooldownSeconds(14.0)
      .build();

   public BloodboundBladeSpell() {
      this.baseManaCost = 35;
      this.manaCostPerLevel = 5;
      this.baseSpellPower = 4;
      this.spellPowerPerLevel = 2;
      this.castTime = 8;
   }

   public CastType getCastType() {
      return CastType.LONG;
   }

   public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
      return this.getCastTime(spellLevel);
   }

   public DefaultConfig getDefaultConfig() {
      return this.defaultConfig;
   }

   public ResourceLocation getSpellResource() {
      return this.spellId;
   }

   public AnimationHolder getCastStartAnimation() {
      return SpellAnimations.THROW_SINGLE_ITEM;
   }

   public AnimationHolder getCastFinishAnimation() {
      return AnimationHolder.pass();
   }

   public Optional<SoundEvent> getCastFinishSound() {
      return Optional.of((SoundEvent)SoundEvents.TRIDENT_THROW.value());
   }

   public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity caster, MagicData playerMagicData) {
      ItemStack sword = this.getSword(caster, playerMagicData);
      if (sword.is(ItemTags.SWORDS)) {
         return true;
      } else {
         if (caster instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("message.somakespells.bloodbound_blade.requires_sword").withStyle(ChatFormatting.RED), true);
         }

         return false;
      }
   }

   public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
      ItemStack sword = this.getSword(caster, playerMagicData);
      if (!sword.is(ItemTags.SWORDS)) {
         if (caster instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("message.somakespells.bloodbound_blade.requires_sword").withStyle(ChatFormatting.RED), true);
         }

         if (caster instanceof ServerPlayer player) {
            Utils.serverSideCancelCast(player, false);
         }
      } else {
         BloodboundBladeProjectileEntity projectile = new BloodboundBladeProjectileEntity(
            level, caster, sword, this.getDamage(spellLevel, caster, sword), this.getRootDuration(spellLevel, caster)
         );
         projectile.shoot(caster.getLookAngle());
         level.addFreshEntity(projectile);
         super.onCast(level, spellLevel, caster, castSource, playerMagicData);
      }
   }

   public void onServerCastTick(Level level, int spellLevel, LivingEntity caster, @Nullable MagicData playerMagicData) {
      if (!this.getSword(caster, playerMagicData).is(ItemTags.SWORDS)) {
         if (caster instanceof ServerPlayer player) {
            player.displayClientMessage(Component.translatable("message.somakespells.bloodbound_blade.requires_sword").withStyle(ChatFormatting.RED), true);
         }

         if (caster instanceof ServerPlayer player) {
            Utils.serverSideCancelCast(player, false);
         }
      }
   }

   public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
      ItemStack sword = caster == null ? ItemStack.EMPTY : caster.getMainHandItem();
      return List.of(
         Component.translatable("ui.irons_spellbooks.damage", new Object[]{this.getDamageText(spellLevel, caster, sword)}),
         Component.translatable("ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(this.getRootDuration(spellLevel, caster), 1)})
      );
   }

   private ItemStack getSword(LivingEntity caster, MagicData magicData) {
      if (caster == null) {
         return ItemStack.EMPTY;
      } else {
         return magicData != null && magicData.getCastingEquipmentSlot().equals(SpellSelectionManager.OFFHAND)
            ? caster.getOffhandItem()
            : caster.getMainHandItem();
      }
   }

   private float getDamage(int spellLevel, @Nullable LivingEntity caster, ItemStack sword) {
      return this.getSpellPower(spellLevel, caster) + this.getSwordDamage(caster, sword);
   }

   private float getSwordDamage(@Nullable LivingEntity caster, ItemStack sword) {
      return caster != null && sword.is(ItemTags.SWORDS)
         ? (float)sword.getAttributeModifiers().compute(caster.getAttributeBaseValue(Attributes.ATTACK_DAMAGE), EquipmentSlot.MAINHAND)
         : 0.0F;
   }

   private int getRootDuration(int spellLevel, @Nullable LivingEntity caster) {
      float bloodPower = caster != null ? (float)this.getSchoolType().getPowerFor(caster) : 1.0F;
      return Mth.clamp(60 + spellLevel * 20 + Math.round(Math.max(0.0F, bloodPower - 1.0F) * 20.0F), 80, 240);
   }

   private String getDamageText(int spellLevel, @Nullable LivingEntity caster, ItemStack sword) {
      float weaponDamage = this.getSwordDamage(caster, sword);
      String plus = weaponDamage > 0.0F ? String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1)) : "";
      return Utils.stringTruncation(this.getDamage(spellLevel, caster, sword), 1) + plus;
   }

   public SpellDamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
      return super.getDamageSource(projectile, attacker).setLifestealPercent(0.1F);
   }
}
