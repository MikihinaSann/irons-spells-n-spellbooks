package net.alshanex.alshanex_familiars.registry;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.alshanex.alshanex_familiars.effects.EndMayhemEffect;
import net.alshanex.alshanex_familiars.effects.SleepyEffect;
import net.alshanex.familiarslib.registry.AttributeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {
   public static final DeferredRegister<MobEffect> MOB_EFFECT_DEFERRED_REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, "alshanex_familiars");
   public static final DeferredHolder<MobEffect, MobEffect> BIRD_BUFF = MOB_EFFECT_DEFERRED_REGISTER.register(
      "bird_buff",
      () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 14270531)
         .addAttributeModifier(
            AttributeRegistry.SOUND_SPELL_POWER,
            new ResourceLocation("alshanex_familiars", "effect.bird_buff_sound_power"),
            0.15,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            io.redspace.ironsspellbooks.api.registry.AttributeRegistry.COOLDOWN_REDUCTION,
            new ResourceLocation("alshanex_familiars", "effect.bird_buff_cast_reduction"),
            0.1,
            Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> SLEEPY = MOB_EFFECT_DEFERRED_REGISTER.register(
      "sleepy",
      () -> new SleepyEffect(MobEffectCategory.HARMFUL, 13695487)
         .addAttributeModifier(
            io.redspace.ironsspellbooks.api.registry.AttributeRegistry.CAST_TIME_REDUCTION,
            new ResourceLocation("alshanex_familiars", "effect.sleepy_cast_reduction"),
            -0.5,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            io.redspace.ironsspellbooks.api.registry.AttributeRegistry.COOLDOWN_REDUCTION,
            new ResourceLocation("alshanex_familiars", "effect.sleepy_cooldown_reduction"),
            -0.25,
            Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            Attributes.MOVEMENT_SPEED, new ResourceLocation("alshanex_familiars", "effect.sleepy_movement_speed"), -0.7, Operation.ADD_MULTIPLIED_TOTAL
         )
         .addAttributeModifier(
            Attributes.ATTACK_SPEED, new ResourceLocation("alshanex_familiars", "effect.sleepy_attack_speed"), -0.7, Operation.ADD_MULTIPLIED_TOTAL
         )
   );
   public static final DeferredHolder<MobEffect, MobEffect> MAYHEM = MOB_EFFECT_DEFERRED_REGISTER.register(
      "end_mayhem", () -> new EndMayhemEffect(MobEffectCategory.HARMFUL, 13695487)
   );

   public static void register(IEventBus eventBus) {
      MOB_EFFECT_DEFERRED_REGISTER.register(eventBus);
   }
}
