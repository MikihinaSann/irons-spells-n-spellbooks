package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Player overrides {@code actuallyHurt} without calling super, so the
 * LivingEntity damage dispatch never sees player hits. Mirrors the same
 * Pre/record/Post wiring for players — no event is posted twice since the
 * LivingEntity WrapMethod is skipped on this path.
 */
@Mixin(Player.class)
public abstract class PlayerDamageDispatchMixin {

    @WrapMethod(method = "actuallyHurt")
    private void neoforge_compat$damageDispatch(DamageSource source, float amount,
            Operation<Void> original) {
        net.neoforged.neoforge.internal.fabric.DamageEventDispatcher.apply(
                (LivingEntity) (Object) this, source, amount,
                (src, adjusted) -> original.call(src, adjusted));
    }

    @ModifyArg(method = "actuallyHurt",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/CombatTracker;recordDamage(Lnet/minecraft/world/damagesource/DamageSource;F)V"),
            index = 1)
    private float neoforge_compat$recordFinalDamage(float amount) {
        return net.neoforged.neoforge.internal.fabric.DamageEventDispatcher.record(
                (LivingEntity) (Object) this, amount);
    }
}
