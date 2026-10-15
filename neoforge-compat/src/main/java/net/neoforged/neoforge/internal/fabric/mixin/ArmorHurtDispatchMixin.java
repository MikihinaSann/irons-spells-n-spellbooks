package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces vanilla {@code doHurtEquipment} with NeoForge's
 * {@link CommonHooks#onArmorHurt} — posts {@code ArmorHurtEvent} with per-slot
 * armor entries, honoring cancel (skip durability loss) and per-slot
 * damage overrides.
 */
@Mixin(LivingEntity.class)
public abstract class ArmorHurtDispatchMixin {

    @Inject(method = "doHurtEquipment", at = @At("HEAD"), cancellable = true)
    private void neoforge$onArmorHurt(DamageSource source, float damage, EquipmentSlot[] slots,
            CallbackInfo ci) {
        ci.cancel();
        CommonHooks.onArmorHurt(source, slots, Math.max(1.0F, damage / 4.0F),
                (LivingEntity) (Object) this);
    }
}
