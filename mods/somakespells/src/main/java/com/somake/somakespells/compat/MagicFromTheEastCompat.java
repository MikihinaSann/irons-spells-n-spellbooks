package com.somake.somakespells.compat;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class MagicFromTheEastCompat {
   public static final String MODID = "iss_magicfromtheeast";
   public static final ResourceLocation SPIRIT_SCHOOL_ID = id("spirit");
   public static final ResourceLocation SYMMETRY_SCHOOL_ID = id("symmetry");
   public static final ResourceLocation SPIRIT_SPELL_POWER_ID = id("spirit_spell_power");
   public static final ResourceLocation SYMMETRY_SPELL_POWER_ID = id("symmetry_spell_power");
   public static final ResourceLocation SOUL_BREAKER_ID = id("soul_breaker");
   public static final ResourceKey<DamageType> SOUL_WEAPONS_BONUS = ResourceKey.create(Registries.DAMAGE_TYPE, id("soul_weapons_bonus"));
   public static final ResourceLocation JADE_SHATTER_PARTICLE_ID = id("jade_shatter");

   private MagicFromTheEastCompat() {
   }

   public static boolean isLoaded() {
      return ModList.get().isLoaded("iss_magicfromtheeast");
   }

   public static ResourceLocation spiritSchoolOrEvocation() {
      return isLoaded() ? SPIRIT_SCHOOL_ID : SchoolRegistry.EVOCATION_RESOURCE;
   }

   public static ResourceLocation symmetrySchoolOrIce() {
      return isLoaded() ? SYMMETRY_SCHOOL_ID : SchoolRegistry.ICE_RESOURCE;
   }

   public static Optional<Reference<Attribute>> getSpiritSpellPowerAttribute() {
      return BuiltInRegistries.ATTRIBUTE.getHolder(SPIRIT_SPELL_POWER_ID);
   }

   public static Holder<Attribute> getSpiritSpellPowerAttributeOrNull() {
      return (Holder<Attribute>)getSpiritSpellPowerAttribute().orElse(null);
   }

   public static Holder<Attribute> getSpiritSpellPowerAttributeOrEvocation() {
      return getSpiritSpellPowerAttribute().map(attribute -> (Holder<Attribute>)attribute).orElse(AttributeRegistry.EVOCATION_SPELL_POWER);
   }

   public static Optional<Reference<Attribute>> getSymmetrySpellPowerAttribute() {
      return BuiltInRegistries.ATTRIBUTE.getHolder(SYMMETRY_SPELL_POWER_ID);
   }

   public static Holder<Attribute> getSymmetrySpellPowerAttributeOrNull() {
      return (Holder<Attribute>)getSymmetrySpellPowerAttribute().orElse(null);
   }

   public static Optional<Item> getSoulBreakerItem() {
      return BuiltInRegistries.ITEM.getOptional(SOUL_BREAKER_ID);
   }

   public static boolean isSoulBreaker(ItemStack stack) {
      return getSoulBreakerItem().<Boolean>map(stack::is).orElse(false);
   }

   public static ParticleOptions getJadeShatterParticle() {
      return BuiltInRegistries.PARTICLE_TYPE.getOptional(JADE_SHATTER_PARTICLE_ID).map(type -> (ParticleOptions)type).orElse(ParticleTypes.CRIT);
   }

   public static Optional<SoundEvent> getSoundEvent(String path) {
      return BuiltInRegistries.SOUND_EVENT.getOptional(id(path));
   }

   public static Optional<SoundEvent> getSpiritCastSound() {
      return getSoundEvent("cast.generic.spirit").or(LegendaryMonstersCompat::getSoulFlySound);
   }

   public static AnimationHolder getKatanaStrikeAnimationOrFallback() {
      return isLoaded() ? new AnimationHolder(id("katana_strike_action"), true) : SpellAnimations.OVERHEAD_MELEE_SWING_ANIMATION;
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("iss_magicfromtheeast", path);
   }
}
