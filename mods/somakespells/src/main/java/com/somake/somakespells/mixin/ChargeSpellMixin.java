package com.somake.somakespells.mixin;

import com.somake.somakespells.config.Config;
import com.somake.somakespells.util.ElementalChargeActivation;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.spells.lightning.ChargeSpell;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ChargeSpell.class})
public abstract class ChargeSpellMixin {
   @Redirect(
      method = {"onCast"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
      ),
      require = 1
   )
   private boolean somake$activateChargeWithElementalBeam(
      LivingEntity target, MobEffectInstance effect, Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData
   ) {
      return ElementalChargeActivation.activate(level, target, effect, 4631295);
   }

   @Inject(
      method = {"getUniqueInfo"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void somake$replaceChargeTooltipWhenPatched(int spellLevel, LivingEntity caster, CallbackInfoReturnable<List<MutableComponent>> cir) {
      if ((Boolean)Config.REMOVE_CHARGE_SPEED_BOOST.get()) {
         ChargeSpell chargeSpell = (ChargeSpell)(Object)this;
         cir.setReturnValue(
            List.of(
               Component.translatable(
                  "ui.irons_spellbooks.effect_length", new Object[]{Utils.timeFromTicks(chargeSpell.getSpellPower(spellLevel, caster) * 20.0F, 1)}
               ),
               Component.translatable(
                  "attribute.modifier.plus.1",
                  new Object[]{
                     Utils.stringTruncation(this.getPercentAttackDamage(spellLevel), 0), Component.translatable("attribute.name.generic.attack_damage")
                  }
               ),
               Component.translatable(
                  "attribute.modifier.plus.1",
                  new Object[]{
                     Utils.stringTruncation(this.getPercentLightningSpellPower(spellLevel), 0),
                     Component.translatable("attribute.irons_spellbooks.lightning_spell_power")
                  }
               )
            )
         );
      }
   }

   private float getPercentAttackDamage(int spellLevel) {
      return spellLevel * 0.05F * 100.0F;
   }

   private float getPercentLightningSpellPower(int spellLevel) {
      return spellLevel * 0.05F * 100.0F;
   }
}
