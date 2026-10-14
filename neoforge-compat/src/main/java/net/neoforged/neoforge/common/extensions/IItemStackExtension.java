package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge-style ItemStack extension methods — injected onto ItemStack via
 * Loom's {@code injected_interfaces}. NOTE: {@link #canElytraFly} and
 * {@link #elytraFlightTick} are also targeted by ISS's
 * {@code IItemExtensionMixin} (interface mixin), so their signatures must match.
 */
public interface IItemStackExtension {

    /**
     * NeoForge's {@code ItemStack.getCraftingRemainingItem()} — the container
     * item left behind after crafting.
     */
    default net.minecraft.world.item.ItemStack getCraftingRemainingItem() {
        net.minecraft.world.item.ItemStack self = (net.minecraft.world.item.ItemStack) (Object) this;
        net.minecraft.world.item.Item remaining = self.getItem().getCraftingRemainingItem();
        return remaining == null ? net.minecraft.world.item.ItemStack.EMPTY : new net.minecraft.world.item.ItemStack(remaining);
    }
    private ItemStack self() {
        return (ItemStack) (Object) this;
    }

    /**
     * Whether the wearer can elytra-fly with this stack in the chest slot.
     * Delegates to the item's {@link IItemExtension#canElytraFly} — the vanilla
     * baseline answers true for a flight-enabled {@code ElytraItem}; overrides
     * (e.g. Apothic's elytra-flight attribute) live on the item side.
     */
    default boolean canElytraFly(LivingEntity entity) {
        return self().getItem().canElytraFly(self(), entity);
    }

    /**
     * Elytra flight tick; return false to stop gliding. Delegates to the item's
     * {@link IItemExtension#elytraFlightTick}; the vanilla default permits
     * continued flight while vanilla itself handles real-elytra durability.
     */
    default boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
        return self().getItem().elytraFlightTick(self(), entity, flightTicks);
    }

    /**
     * Called when this stack is destroyed by a container/crafting.
     */
    default void onDestroyed(ItemEntity itemEntity) {
    }

    default void onDestroyed(ItemEntity itemEntity, net.minecraft.world.damagesource.DamageSource source) {
    }

    default void onItemUseFirst(net.minecraft.world.item.context.UseOnContext context) {
    }

    /**
     * Whether the stack can be enchanted with the given enchantment (by holder).
     */
    default boolean canApplyAtEnchantingTable(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return true;
    }

    default boolean canPerformAction(net.neoforged.neoforge.common.ItemAbility action) {
        return false;
    }

    @Nullable
    default Float getSweepHitBox(@Nullable Player player, @Nullable Entity target) {
        return null;
    }

    default boolean onEntityItemUpdate(ItemEntity entity) {
        return false;
    }

    default boolean doesSneakBypassUse(net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, Player player) {
        return false;
    }

    default int getEntityLifespan(Level level) {
        return 6000;
    }

    /**
     * Data-component accessors accepting NeoForge's holder-supplier form
     * (used when a {@code DeferredHolder} is passed instead of the
     * {@code DataComponentType} itself).
     */
    default <T> T get(java.util.function.Supplier<? extends net.minecraft.core.component.DataComponentType<? extends T>> type) {
        return self().get(type.get());
    }

    default <T> T getOrDefault(java.util.function.Supplier<? extends net.minecraft.core.component.DataComponentType<? extends T>> type, T fallback) {
        return self().getOrDefault(type.get(), fallback);
    }

    default <T> void set(java.util.function.Supplier<? extends net.minecraft.core.component.DataComponentType<? super T>> type, T value) {
        self().set(type.get(), value);
    }

    default <T> void remove(java.util.function.Supplier<? extends net.minecraft.core.component.DataComponentType<? extends T>> type) {
        self().remove(type.get());
    }

    default <T> boolean has(java.util.function.Supplier<? extends net.minecraft.core.component.DataComponentType<? extends T>> type) {
        return self().has(type.get());
    }

    @SuppressWarnings("unchecked")
    default <T> T update(java.util.function.Supplier<? extends net.minecraft.core.component.DataComponentType<? extends T>> type, T fallback, java.util.function.UnaryOperator<T> updater) {
        return self().update((net.minecraft.core.component.DataComponentType<T>) type.get(), fallback, updater);
    }

    default boolean onDroppedByPlayer(Player player) {
        return true;
    }

    /**
     * Enchantment level for a holder (NeoForge adds this to ItemStack; vanilla
     * routes through {@code EnchantmentHelper}).
     */
    default int getEnchantmentLevel(net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(enchantment, self());
    }

    /**
     * Whether this stack can be equipped in the given slot for the entity.
     */
    default boolean canEquip(net.minecraft.world.entity.EquipmentSlot equipmentSlot, net.minecraft.world.entity.LivingEntity entity) {
        return entity.getEquipmentSlotForItem(self()) == equipmentSlot;
    }

    /**
     * NeoForge's entity-aware food properties lookup.
     */
    default net.minecraft.world.food.FoodProperties getFoodProperties(net.minecraft.world.entity.LivingEntity entity) {
        return self().get(net.minecraft.core.component.DataComponents.FOOD);
    }

    /**
     * Attribute modifiers component (NeoForge kept the pre-component name).
     */
    default net.minecraft.world.item.component.ItemAttributeModifiers getAttributeModifiers() {
        return self().getOrDefault(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY);
    }

    /**
     * NeoForge shield-disable check on the attacking weapon stack.
     */
    default boolean canDisableShield(net.minecraft.world.item.ItemStack shield,
            net.minecraft.world.entity.LivingEntity entity, net.minecraft.world.entity.LivingEntity attacker) {
        return self().getItem().canDisableShield(self(), shield, entity, attacker);
    }

    /**
     * NeoForge widens vanilla's {@code hurtAndBreak(int, ServerLevel, ServerPlayer, Consumer)}
     * to accept any {@link LivingEntity}. For non-players the durability change is
     * applied directly since vanilla's overload requires a ServerPlayer.
     */
    default void hurtAndBreak(int amount, net.minecraft.server.level.ServerLevel level,
            net.minecraft.world.entity.LivingEntity entity,
            java.util.function.Consumer<net.minecraft.world.item.Item> onBreak) {
        net.minecraft.world.item.ItemStack stack = self();
        if (entity instanceof net.minecraft.server.level.ServerPlayer player) {
            stack.hurtAndBreak(amount, level, player, onBreak);
            return;
        }
        if (entity.hasInfiniteMaterials() || !stack.isDamageableItem()) {
            return;
        }
        int damage = net.minecraft.world.item.enchantment.EnchantmentHelper.processDurabilityChange(
                level, stack, amount);
        if (damage <= 0) {
            return;
        }
        int newDamage = stack.getDamageValue() + damage;
        stack.setDamageValue(newDamage);
        if (newDamage >= stack.getMaxDamage()) {
            net.minecraft.world.item.Item item = stack.getItem();
            stack.shrink(1);
            stack.setDamageValue(0);
            onBreak.accept(item);
        }
    }
}
