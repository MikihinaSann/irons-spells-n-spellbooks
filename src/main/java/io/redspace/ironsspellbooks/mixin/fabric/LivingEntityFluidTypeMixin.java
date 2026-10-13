package io.redspace.ironsspellbooks.mixin.fabric;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * NeoForge's {@code IEntityExtension.canDrownInFluidType} feeds the air-supply
 * check: an entity that cannot drown in water may breathe underwater.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityFluidTypeMixin {

    @Inject(method = "canBreatheUnderwater", at = @At("RETURN"), cancellable = true)
    private void irons_spellbooks$cannotDrownMeansBreathe(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && !((Entity) (Object) this).canDrownInFluidType(NeoForgeMod.WATER_TYPE)) {
            cir.setReturnValue(true);
        }
    }
}
