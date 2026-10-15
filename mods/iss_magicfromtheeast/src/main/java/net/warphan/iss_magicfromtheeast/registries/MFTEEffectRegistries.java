package net.warphan.iss_magicfromtheeast.registries;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;
import net.warphan.iss_magicfromtheeast.effects.AnchoredSoulEffect;
import net.warphan.iss_magicfromtheeast.effects.MistStepEffect;
import net.warphan.iss_magicfromtheeast.effects.ReversalHealingEffect;
import net.warphan.iss_magicfromtheeast.effects.SoulburnEffect;

public class MFTEEffectRegistries {
   public static final DeferredRegister<MobEffect> MFTE_MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, "iss_magicfromtheeast");
   public static final DeferredHolder<MobEffect, MobEffect> CLOUD_BLESS_EFFECT = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "cloud_bless",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 12495141)
         .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, ISS_MagicFromTheEast.id("mobeffect_cloud_bless"), 999.0, Operation.ADD_VALUE)
   );
   public static final DeferredHolder<MobEffect, MobEffect> SOULBURN = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "soulburn", () -> new SoulburnEffect(MobEffectCategory.HARMFUL, 65535)
   );
   public static final DeferredHolder<MobEffect, MobEffect> REVERSAL_HEALING = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "reversal_healing",
      () -> new ReversalHealingEffect(MobEffectCategory.BENEFICIAL, 47991)
         .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ISS_MagicFromTheEast.id("mobeffect_reversal_healing"), 0.05F, Operation.ADD_VALUE)
   );
   public static final DeferredHolder<MobEffect, MobEffect> MIST_STEP = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "mist_step",
      () -> new MistStepEffect(MobEffectCategory.BENEFICIAL, 8356754)
         .addAttributeModifier(Attributes.MOVEMENT_SPEED, ISS_MagicFromTheEast.id("mobeffect_mist_speed"), 0.5, Operation.ADD_MULTIPLIED_BASE)
         .addAttributeModifier(Attributes.STEP_HEIGHT, ISS_MagicFromTheEast.id("mobeffect_mist_step"), 0.5, Operation.ADD_VALUE)
         .addAttributeModifier(Attributes.JUMP_STRENGTH, ISS_MagicFromTheEast.id("mobeffect_mist_jump"), 0.5, Operation.ADD_MULTIPLIED_BASE)
         .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, ISS_MagicFromTheEast.id("mobeffect_mist_safe_fall"), 8.0, Operation.ADD_VALUE)
         .addAttributeModifier(Attributes.WATER_MOVEMENT_EFFICIENCY, ISS_MagicFromTheEast.id("mobeffect_mist_water_speed"), 0.5, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final DeferredHolder<MobEffect, MobEffect> ANCHORED_SOUL = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "anchored_soul", () -> new AnchoredSoulEffect(MobEffectCategory.HARMFUL, 65535)
   );
   public static final DeferredHolder<MobEffect, MobEffect> FLAG_FIRE = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "effect_banner_red",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 16724995)
         .addAttributeModifier(Attributes.ATTACK_DAMAGE, ISS_MagicFromTheEast.id("mobeffect_flag_fire_damage"), 0.1F, Operation.ADD_MULTIPLIED_BASE)
         .addAttributeModifier(Attributes.ATTACK_SPEED, ISS_MagicFromTheEast.id("mobeffect_flag_fire_attack_speed"), 0.1F, Operation.ADD_MULTIPLIED_BASE)
   );
   public static final DeferredHolder<MobEffect, MobEffect> FLAG_NATURE = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "effect_banner_green",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 32768)
         .addAttributeModifier(Attributes.ARMOR, ISS_MagicFromTheEast.id("mobeffect_flag_nature_armor"), 4.0, Operation.ADD_VALUE)
         .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ISS_MagicFromTheEast.id("mobeffect_flag_nature_knockback_resit"), 0.1F, Operation.ADD_VALUE)
   );
   public static final DeferredHolder<MobEffect, MobEffect> FLAG_LIGHTNING = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "effect_banner_white",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 5639085)
         .addAttributeModifier(Attributes.MOVEMENT_SPEED, ISS_MagicFromTheEast.id("mobeffect_flag_lightning_move_speed"), 0.1F, Operation.ADD_MULTIPLIED_BASE)
         .addAttributeModifier(
            AttributeRegistry.MANA_REGEN, ISS_MagicFromTheEast.id("mobeffect_flag_lightning_mana_regen"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> FLAG_ICE = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "effect_banner_black",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 4756975)
         .addAttributeModifier(AttributeRegistry.SPELL_RESIST, ISS_MagicFromTheEast.id("mobeffect_flag_ice_spell_resit"), 0.1F, Operation.ADD_MULTIPLIED_BASE)
         .addAttributeModifier(
            AttributeRegistry.COOLDOWN_REDUCTION, ISS_MagicFromTheEast.id("mobeffect_flag_ice_cd_reduction"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> FLAG_HOLY = MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(
      "effect_banner_yellow",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 16765697)
         .addAttributeModifier(Attributes.MAX_HEALTH, ISS_MagicFromTheEast.id("mobeffect_flag_holy_health"), 4.0, Operation.ADD_VALUE)
         .addAttributeModifier(
            AttributeRegistry.CAST_TIME_REDUCTION, ISS_MagicFromTheEast.id("mobeffect_flag_holy_cast_time"), 0.1F, Operation.ADD_MULTIPLIED_BASE
         )
   );

   public static void register(IEventBus eventBus) {
      MFTE_MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
   }
}
