package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's {@code IEntityExtension.canDrownInFluidType} feeds the air-supply
 * check: an entity that cannot drown in water may breathe underwater. The
 * reentrancy flag prevents infinite recursion because the fluid-type check
 * itself consults {@code canBreatheUnderwater}.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityFluidTypeMixin {

    @org.spongepowered.asm.mixin.Unique
    private boolean neoforge_compat$drownCheck;

    @Inject(method = "canBreatheUnderwater", at = @At("RETURN"), cancellable = true)
    private void irons_spellbooks$cannotDrownMeansBreathe(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && !neoforge_compat$drownCheck) {
            neoforge_compat$drownCheck = true;
            try {
                if (!((Entity) (Object) this).canDrownInFluidType(NeoForgeMod.WATER_TYPE)) {
                    cir.setReturnValue(true);
                }
            } finally {
                neoforge_compat$drownCheck = false;
            }
        }
    }
}
