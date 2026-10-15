package net.alshanex.tunes_n_tomes.registry;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.tunes_n_tomes.effect.CheeredEffect;
import net.alshanex.tunes_n_tomes.effect.DeafenedEffect;
import net.alshanex.tunes_n_tomes.effect.EncoreEffect;
import net.alshanex.tunes_n_tomes.effect.HardenedEffect;
import net.alshanex.tunes_n_tomes.effect.LastStandEffect;
import net.alshanex.tunes_n_tomes.effect.SleepyEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TEffectRegistry {
   public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, "tunes_n_tomes");
   public static final DeferredHolder<MobEffect, MobEffect> CHEERED = MOB_EFFECT_DEFERRED_REGISTER.register(
      "cheered",
      () -> new CheeredEffect(MobEffectCategory.BENEFICIAL, 14270531)
         .addAttributeModifier(
            TAttributeRegistry.MELODY_SPELL_POWER,
            ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "effect.bird_buff_melody_power"),
            0.15,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            AttributeRegistry.COOLDOWN_REDUCTION,
            ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "effect.bird_buff_cast_reduction"),
            0.1,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> SLEEPY = MOB_EFFECT_DEFERRED_REGISTER.register(
      "sleepy",
      () -> new SleepyEffect(MobEffectCategory.HARMFUL, 13695487)
         .addAttributeModifier(
            AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "sleepy.mana_regen"), -0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            AttributeRegistry.COOLDOWN_REDUCTION,
            ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "effect.sleepy_cooldown_reduction"),
            -0.25,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "effect.sleepy_movement_speed"),
            -0.7,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "effect.sleepy_attack_speed"), -0.7, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> ENCORE = MOB_EFFECT_DEFERRED_REGISTER.register(
      "encore", () -> new EncoreEffect(MobEffectCategory.HARMFUL, 13695487)
   );
   public static final DeferredHolder<MobEffect, MobEffect> DEAFENED = MOB_EFFECT_DEFERRED_REGISTER.register(
      "deafened",
      () -> new DeafenedEffect(MobEffectCategory.HARMFUL, 13695487)
         .addAttributeModifier(
            Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "deafened.slowness"), -0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> VALOR = MOB_EFFECT_DEFERRED_REGISTER.register(
      "valor",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 14270531)
         .addAttributeModifier(
            Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "valor.speed"), 0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "valor.speed"), 0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "valor.mana_regen"), 0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> HARDENED = MOB_EFFECT_DEFERRED_REGISTER.register(
      "hardened",
      () -> new HardenedEffect(MobEffectCategory.BENEFICIAL, 14270531)
         .addAttributeModifier(
            Attributes.MAX_ABSORPTION, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "hardened.absorption"), 1.0, Operation.ADD_VALUE
         )
         .addAttributeModifier(
            AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "hardened.mana_regen"), 0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> LAST_STAND = MOB_EFFECT_DEFERRED_REGISTER.register(
      "last_stand",
      () -> new LastStandEffect(MobEffectCategory.BENEFICIAL, 14270531)
         .addAttributeModifier(
            AttributeRegistry.MANA_REGEN, ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "last_stand.mana_regen"), -0.1, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            AttributeRegistry.COOLDOWN_REDUCTION,
            ResourceLocation.fromNamespaceAndPath("tunes_n_tomes", "last_stand.cooldown_reduction"),
            -0.1,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );

   public static void register(IEventBus eventBus) {
      MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
   }
}
