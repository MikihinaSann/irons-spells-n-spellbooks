/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.common.extensions;

import net.neoforged.neoforge.entity.IEntityExtension;

import java.util.Iterator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public interface ILivingEntityExtension extends IEntityExtension {
    @Override
    default LivingEntity self() {
        return (LivingEntity) IEntityExtension.super.self();
    }

    /**
     * NeoForge's damage-container stack, live during {@code hurt}. The real
     * implementation is added by DamageContainersMixin, which overrides this
     * default (a mixin can't be reached by @Shadow, hence the accessor).
     */
    default java.util.Stack<DamageContainer> getDamageContainers() {
        throw new AssertionError("implemented by DamageContainersMixin");
    }

    @Override
    default boolean canSwimInFluidType(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value()) return !self().isSensitiveToWater();
        else return IEntityExtension.super.canSwimInFluidType(type);
    }

    /**
     * Performs what to do when an entity attempts to go up or "jump" in a fluid.
     *
     * @param type the type of the fluid
     */
    default void jumpInFluid(FluidType type) {
        // Apply swim speed only to WATER fluid types
        double multiplier = type == NeoForgeMod.WATER_TYPE.value() ? self().getAttributeValue(NeoForgeMod.SWIM_SPEED) : 1.0D;
        self().setDeltaMovement(self().getDeltaMovement().add(0.0D, (double) 0.04F * multiplier, 0.0D));
    }

    /**
     * Performs what to do when an entity attempts to go down or "sink" in a fluid.
     *
     * @param type the type of the fluid
     */
    default void sinkInFluid(FluidType type) {
        // Apply swim speed only to WATER fluid types
        double multiplier = type == NeoForgeMod.WATER_TYPE.value() ? self().getAttributeValue(NeoForgeMod.SWIM_SPEED) : 1.0D;
        self().setDeltaMovement(self().getDeltaMovement().add(0.0D, (double) -0.04F * multiplier, 0.0D));
    }

    /**
     * Returns whether the entity can drown in the fluid.
     *
     * @param type the type of the fluid
     * @return {@code true} if the entity can drown in the fluid, {@code false} otherwise
     */
    default boolean canDrownInFluidType(FluidType type) {
        if (type == NeoForgeMod.WATER_TYPE.value()) return !self().canBreatheUnderwater();
        return type.canDrownIn(self());
    }

    /**
     * Performs how an entity moves when within the fluid. If using custom
     * movement logic, the method should return {@code true}. Otherwise, the
     * movement logic will default to water.
     *
     * @param state          the state of the fluid
     * @param movementVector the velocity of how the entity wants to move
     * @param gravity        the gravity to apply to the entity
     * @return {@code true} if custom movement logic is performed, {@code false} otherwise
     */
    default boolean moveInFluid(FluidState state, Vec3 movementVector, double gravity) {
        return state.getType().getFluidType().move(state, self(), movementVector, gravity);
    }

    /**
     * Executes in {@link LivingEntity#hurt(DamageSource, float)} after all damage and
     * effects have applied. Overriding this method is preferred over overriding the
     * hurt method in custom entities where special behavior is desired after vanilla
     * logic.
     *
     * @param damageContainer The aggregated damage details preceding this hook, which
     *                        includes changes made to the damage sequence by events.
     */
    default void onDamageTaken(DamageContainer damageContainer) {}

    /**
     * Removes all potion effects that have the given {@link EffectCure} in their set of cures.
     *
     * <p>Fires {@code MobEffectEvent.Remove} per effect; a canceled event keeps the effect.
     * NeoForge adds this method to {@link LivingEntity}; on Fabric it is provided via the
     * injected interface.
     *
     * @param cure the cure to match
     * @return true if at least one effect was removed
     */
    default boolean removeEffectsCuredBy(EffectCure cure) {
        LivingEntity self = self();
        if (self.level().isClientSide) return false;
        boolean ret = false;
        Iterator<MobEffectInstance> itr = self.getActiveEffectsMap().values().iterator();
        while (itr.hasNext()) {
            MobEffectInstance effect = itr.next();
            // LivingEntity#onEffectRemoved is private in vanilla, widened by neoforge_compat.accesswidener
            if (((IMobEffectInstanceExtension) effect).getCures().contains(cure)
                    && !NeoForge.EVENT_BUS.post(
                            new net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove(self, effect, cure)).isCanceled()) {
                self.onEffectRemoved(effect);
                itr.remove();
                ret = true;
                self.effectsDirty = true;
            }
        }
        return ret;
    }
}
