package com.chronomancy.mixin;

import com.chronomancy.temporal.TemporalPlayerClock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Effects tick through vanilla so periodic healing, poison and expiration keep their semantics. */
@Mixin(LivingEntity.class)
public abstract class PersonalTimeLivingMixin {
    @Shadow protected abstract void tickEffects();
    @Shadow protected abstract void updateUsingItem(ItemStack stack);
    @Shadow protected abstract int decreaseAirSupply(int air);
    @Shadow protected abstract int increaseAirSupply(int air);

    @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tickEffects()V"))
    private void chronomancy$effects(LivingEntity entity) {
        int steps = entity instanceof ServerPlayer player ? TemporalPlayerClock.steps(player) : 1;
        for (int i = 0; i < steps; i++) this.tickEffects();
    }

    @Redirect(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updateUsingItem(Lnet/minecraft/world/item/ItemStack;)V"))
    private void chronomancy$consume(LivingEntity entity, ItemStack stack) {
        boolean consumable = stack.getUseAnimation() == UseAnim.EAT || stack.getUseAnimation() == UseAnim.DRINK;
        int steps = consumable && entity instanceof ServerPlayer player ? TemporalPlayerClock.steps(player) : 1;
        if (consumable && entity.level().isClientSide() && entity instanceof net.minecraft.world.entity.player.Player player) {
            steps = com.chronomancy.client.BorrowedFutureClientState.consumptionSteps(player);
        }
        for (int i = 0; i < steps && entity.isUsingItem(); i++) this.updateUsingItem(stack);
    }

    /**
     * Extra air-handling steps for time-accelerated entities. Runs after
     * vanilla's own breathe pass (which neoforge_compat routes through
     * {@link CommonHooks#onLivingBreathe}), so each additional accelerated
     * tick re-runs the full NeoForge breathe/drown logic. Upstream redirected
     * the in-method NeoForge call site; a TAIL inject is used here instead so
     * it does not depend on mixin ordering.
     */
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void chronomancy$breatheExtra(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        int steps = entity instanceof ServerPlayer player ? TemporalPlayerClock.steps(player) : 1;
        for (int i = 1; i < steps; i++) {
            int airSupply = entity.getAirSupply();
            CommonHooks.onLivingBreathe(entity, airSupply - this.decreaseAirSupply(airSupply),
                    this.increaseAirSupply(airSupply) - airSupply);
        }
    }
}
