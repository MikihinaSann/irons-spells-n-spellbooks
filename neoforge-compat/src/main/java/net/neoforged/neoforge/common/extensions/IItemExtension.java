package net.neoforged.neoforge.common.extensions;

import net.minecraft.world.item.Item;

/**
 * NeoForge-style Item extension — injected onto Item via Loom
 * {@code injected_interfaces}.
 */
public interface IItemExtension {
    private Item self() {
        return (Item) this;
    }

    default boolean canElytraFly(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        // Vanilla baseline: only an ElytraItem in good repair grants flight.
        // (Delegating to stack.canElytraFly here would recurse — the stack-level
        // extension delegates back to the item.)
        return stack.getItem() instanceof net.minecraft.world.item.ElytraItem
                && net.minecraft.world.item.ElytraItem.isFlyEnabled(stack);
    }

    /**
     * NeoForge's elytraFlightTick — called every tick while elytra-flying; return
     * false to stop flight. Vanilla has no per-item hook, so the default always
     * permits continued flight.
     */
    default boolean elytraFlightTick(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.LivingEntity entity, int flightTicks) {
        return true;
    }

    /**
     * Called when an ItemEntity containing this stack ticks. Return true to
     * consume the vanilla tick (preventing pickup/despawn handling).
     */
    default boolean onEntityItemUpdate(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.item.ItemEntity entity) {
        return false;
    }

    /**
     * Whether this stack can be used to perform the given item ability.
     */
    default boolean canPerformAction(net.minecraft.world.item.ItemStack stack, net.neoforged.neoforge.common.ItemAbility action) {
        return false;
    }

    /**
     * Repair ingredients / repairability check.
     */
    default boolean isRepairable(net.minecraft.world.item.ItemStack stack) {
        return stack.isEnchantable() || stack.get(net.minecraft.core.component.DataComponents.REPAIR_COST) != null;
    }

    /**
     * Whether the stack counts as an "ender mask" (prevents enderman aggro).
     */
    default boolean isEnderMask(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.monster.EnderMan endermanEntity) {
        return self().equals(net.minecraft.world.item.Items.CARVED_PUMPKIN);
    }

    /**
     * Mod id of the mod that created this item (for creative tab attribution).
     */
    @org.jetbrains.annotations.Nullable
    default String getCreatorModId(net.minecraft.world.item.ItemStack stack) {
        var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(self());
        return key.getNamespace();
    }

    /**
     * Whether this item supports the given enchantment on this stack.
     */
    default boolean supportsEnchantment(net.minecraft.world.item.ItemStack stack, net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return enchantment.value().canEnchant(stack);
    }

    /**
     * Enchantment value for this stack.
     */
    default int getEnchantmentValue(net.minecraft.world.item.ItemStack stack) {
        return self().getEnchantmentValue();
    }

    /**
     * Whether the held item should re-equip animate when the stack changes.
     */
    default boolean shouldCauseReequipAnimation(net.minecraft.world.item.ItemStack oldStack, net.minecraft.world.item.ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack);
    }

    /**
     * Whether this stack can walk on powdered snow for the wearer.
     */
    default boolean canWalkOnPowderedSnow(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.LivingEntity wearer) {
        return stack.is(net.minecraft.world.item.Items.LEATHER_BOOTS);
    }

    /**
     * Whether this stack creates a custom entity when dropped.
     */
    default boolean hasCustomEntity(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    /**
     * Creates the dropped entity for this stack when {@link #hasCustomEntity} is true.
     */
    @org.jetbrains.annotations.Nullable
    default net.minecraft.world.entity.Entity createEntity(net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity, net.minecraft.world.item.ItemStack stack) {
        return null;
    }

    /**
     * Applies a patch to this item's default component map.
     */
    default void modifyDefaultComponentsFrom(net.minecraft.core.component.DataComponentPatch patch) {
        var components = self().components();
        if (components instanceof net.minecraft.core.component.PatchedDataComponentMap patched) {
            patched.applyPatch(patch);
        }
    }

    /**
     * NeoForge's per-item client-extension hook — invoked once during client
     * init so items can provide their {@link IClientItemExtensions}.
     */
    default void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientItemExtensions> consumer) {
    }
}
