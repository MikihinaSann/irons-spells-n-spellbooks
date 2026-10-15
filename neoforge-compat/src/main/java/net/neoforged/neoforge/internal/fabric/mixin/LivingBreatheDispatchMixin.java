package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Converts {@code LivingEntity.baseTick}'s inline air-supply block into
 * NeoForge's form: a single {@code CommonHooks.onLivingBreathe(entity, consume,
 * refill)} call that posts {@code LivingBreatheEvent}/{@code LivingDrownEvent}
 * and handles fluid-typed breathing via {@code canDrownInFluidType}.
 * <p>
 * The vanilla {@code if (isEyeInFluid(WATER) && !BUBBLE) ... else if (air <
 * max) ...} region is neutralized: the fluid check is forced false and the
 * else-branch refill becomes a no-op, leaving the hook as the sole air handler
 * (same semantics, plus NeoForge fluid-type support).
 */
@Mixin(LivingEntity.class)
public abstract class LivingBreatheDispatchMixin {

    @Shadow
    protected abstract int decreaseAirSupply(int air);

    @Shadow
    protected abstract int increaseAirSupply(int air);

    /**
     * Run the NeoForge breathe hook at the head of the vanilla air-handling
     * block. consume = air lost this tick (decreaseAirSupply delta), refill =
     * air regained (increaseAirSupply delta) — matching NeoForge's patch.
     */
    @Inject(method = "baseTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private void neoforge$onLivingBreathe(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        int airSupply = self.getAirSupply();
        CommonHooks.onLivingBreathe(self, airSupply - this.decreaseAirSupply(airSupply),
                this.increaseAirSupply(airSupply) - airSupply);
    }

    /** Skip the vanilla drowning/dismount branch — handled by the hook. */
    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean neoforge$skipVanillaAirBlock(boolean original) {
        return false;
    }

    /** Neutralize the else-branch refill — the hook already applied it. */
    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;increaseAirSupply(I)I"))
    private int neoforge$skipVanillaRefill(LivingEntity self, int air,
            Operation<Integer> original) {
        return self.getAirSupply();
    }
}
