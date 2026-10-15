package com.somake.somakespells.effect.spirit;

import com.somake.somakespells.compat.MagicFromTheEastCompat;
import com.somake.somakespells.util.RedSoulVisuals;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class RedSoulEffect extends MagicMobEffect {
   public static final int BASE_DURATION_TICKS = 7200;
   public static final int MAX_DURATION_TICKS = 7200;
   public static final int WINGED_RUIN_COST_TICKS = 400;
   public static final double SPIRIT_SPELL_POWER_BONUS = 0.25;
   private static final Map<UUID, RedSoulEffect.VisualState> VISUAL_STATES = new ConcurrentHashMap<>();

   public RedSoulEffect(MobEffectCategory category, int color) {
      super(category, color);
      this.addAttributeModifier(
         MagicFromTheEastCompat.getSpiritSpellPowerAttributeOrEvocation(),
         ResourceLocation.fromNamespaceAndPath("somakespells", "effect.red_soul.spirit_spell_power"),
         0.25,
         Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public void onEffectAdded(LivingEntity entity, int amplifier) {
      super.onEffectAdded(entity, amplifier);
      if (!entity.level().isClientSide) {
         VISUAL_STATES.put(entity.getUUID(), new RedSoulEffect.VisualState());
         RedSoulVisuals.beginActivation(entity);
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide) {
         RedSoulEffect.VisualState state = VISUAL_STATES.computeIfAbsent(entity.getUUID(), ignored -> RedSoulEffect.VisualState.mature());
         state.activeTicks++;
         if (state.activeTicks <= 9) {
            RedSoulVisuals.tickActivation(entity, state.activeTicks);
         }

         if (entity.onGround() && entity.getDeltaMovement().horizontalDistanceSqr() < 0.0025) {
            state.idleTicks++;
         } else {
            state.idleTicks = 0;
         }

         RedSoulVisuals.tickAura(entity, state.idleTicks);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public void onEffectRemoved(LivingEntity entity, int amplifier) {
      super.onEffectRemoved(entity, amplifier);
      if (!entity.level().isClientSide) {
         VISUAL_STATES.remove(entity.getUUID());
         RedSoulVisuals.deactivate(entity);
      }
   }

   private static final class VisualState {
      private int activeTicks;
      private int idleTicks;

      private static RedSoulEffect.VisualState mature() {
         RedSoulEffect.VisualState state = new RedSoulEffect.VisualState();
         state.activeTicks = 10;
         return state;
      }
   }
}
