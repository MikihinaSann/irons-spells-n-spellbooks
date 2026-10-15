package com.somake.somakespells.mixin;

import com.somake.somakespells.entity.IronSpellDamageEntity;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Projectile.SoulPillarEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({SoulPillarEntity.class})
public abstract class SoulPillarEntityMixin {
   @Redirect(
      method = {"damage"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )
   )
   private boolean somake$useIronSpellDamage(LivingEntity target, DamageSource source, float amount) {
      return (Object)this instanceof IronSpellDamageEntity spellDamageEntity ? spellDamageEntity.somake$applySpellDamage(target, amount) : target.hurt(source, amount);
   }
}
