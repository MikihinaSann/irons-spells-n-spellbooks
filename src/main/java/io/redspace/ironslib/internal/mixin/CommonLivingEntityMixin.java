package io.redspace.ironslib.internal.mixin;

import io.redspace.ironslib.registry.IronsLibRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({LivingEntity.class})
public class CommonLivingEntityMixin {
   @Redirect(
      method = {"getDamageAfterArmorAbsorb"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getArmorValue()I"
      )
   )
   private int irons_lib$applyArmorPierce(LivingEntity target, DamageSource damageSource, float damageAmount) {
      int armor = target.getArmorValue();
      if (damageSource.getEntity() instanceof LivingEntity attacker && armor > 0) {
         double armorPierce = attacker.getAttributeValue(IronsLibRegistries.AttributeRegistry.ARMOR_PIERCE);
         if (armorPierce <= 0.0) {
            return armor;
         }

         return Math.max(0, (int)Math.floor((double)armor - armorPierce));
      }

      return armor;
   }
}
