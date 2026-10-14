package net.neoforged.neoforge.common.util;

import java.util.function.Predicate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;

/**
 * Stand-in for the predicate overloads NeoForge patches onto
 * {@link net.minecraft.world.entity.projectile.ProjectileUtil}. Static methods
 * cannot be added via interface injection, so call sites use this helper.
 */
public final class NeoForgeProjectileUtil {
    private NeoForgeProjectileUtil() {
    }

    /**
     * NeoForge's {@code ProjectileUtil#getWeaponHoldingHand(LivingEntity, Predicate)} —
     * returns the hand holding an item matching the predicate, preferring the main hand.
     */
    public static InteractionHand getWeaponHoldingHand(LivingEntity entity, Predicate<Item> predicate) {
        if (predicate.test(entity.getMainHandItem().getItem())) {
            return InteractionHand.MAIN_HAND;
        }
        return predicate.test(entity.getOffhandItem().getItem()) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }
}
