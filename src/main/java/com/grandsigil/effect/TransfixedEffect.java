package com.grandsigil.effect;

import com.grandsigil.GrandSigil;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class TransfixedEffect extends MobEffect {
   public static final int COLOUR = 7024331;

   public TransfixedEffect() {
      super(MobEffectCategory.HARMFUL, 7024331);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, GrandSigil.id("transfixed_hold"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FLYING_SPEED, GrandSigil.id("transfixed_hover"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   @Override
   public boolean applyEffectTick(LivingEntity target, int amplifier) {
      hold(target);
      return true;
   }

   public static void hold(LivingEntity target) {
      if (!target.level().isClientSide) {
         Restraint.pin(target);
         silence(target);
      }
   }

   private static void silence(LivingEntity target) {
      if (target instanceof ServerPlayer player) {
         if (MagicData.getPlayerMagicData(player).isCasting()) {
            Utils.serverSideCancelCast(player);
         }
      } else if (target instanceof IMagicEntity caster && caster.isCasting()) {
         caster.cancelCast();
      }
   }
}
