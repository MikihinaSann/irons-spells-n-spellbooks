package com.somake.somakespells.mixin;

import com.somake.somakespells.event.ElementalAttributeDamageHandler;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityDamageMixin {
   @Shadow
   protected float lastHurt;
   @Unique
   private final Deque<Optional<LivingEntity>> somake$elementalAttackers = new ArrayDeque<>();

   @Inject(
      method = {"hurt"},
      at = {@At("HEAD")}
   )
   private void somake$captureElementalDamageContext(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      LivingEntity target = (LivingEntity)(Object)this;
      LivingEntity attacker = ElementalAttributeDamageHandler.getEligibleAttacker(target, source, amount);
      this.somake$elementalAttackers.push(Optional.ofNullable(attacker));
   }

   @Inject(
      method = {"hurt"},
      at = {@At("RETURN")}
   )
   private void somake$applyElementalDamageAfterAcceptedHit(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      if (!this.somake$elementalAttackers.isEmpty()) {
         Optional<LivingEntity> attacker = this.somake$elementalAttackers.pop();
         if (attacker.isPresent() && Boolean.TRUE.equals(cir.getReturnValue())) {
            LivingEntity target = (LivingEntity)(Object)this;
            int postPhysicalInvulnerability = target.invulnerableTime;
            float postPhysicalLastHurt = this.lastHurt;

            try {
               ElementalAttributeDamageHandler.applyAuxiliaryDamages(attacker.get(), target);
            } finally {
               target.invulnerableTime = postPhysicalInvulnerability;
               this.lastHurt = postPhysicalLastHurt;
            }
         }
      }
   }
}
