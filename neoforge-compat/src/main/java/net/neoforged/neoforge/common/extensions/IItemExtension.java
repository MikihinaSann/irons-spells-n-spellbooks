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

    /**
     * Whether this item (as a weapon stack) can disable the defender's shield.
     * NeoForge's default: {@code instanceof AxeItem}.
     */
    default boolean canDisableShield(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.item.ItemStack shield,
            net.minecraft.world.entity.LivingEntity entity,
            net.minecraft.world.entity.LivingEntity attacker) {
        return this instanceof net.minecraft.world.item.AxeItem;
    }

    /**
     * Stack-aware damage: NeoForge reads the DAMAGE component clamped to max.
     */
    default int getDamage(net.minecraft.world.item.ItemStack stack) {
        return net.minecraft.util.Mth.clamp(stack.getOrDefault(net.minecraft.core.component.DataComponents.DAMAGE, 0),
                0, stack.getMaxDamage());
    }

    /**
     * Stack-aware damage write: NeoForge writes the DAMAGE component clamped to max.
     */
    default void setDamage(net.minecraft.world.item.ItemStack stack, int damage) {
        stack.set(net.minecraft.core.component.DataComponents.DAMAGE,
                net.minecraft.util.Mth.clamp(damage, 0, stack.getMaxDamage()));
    }

    /**
     * Stack-aware max damage: NeoForge reads the MAX_DAMAGE component (0 when absent).
     */
    default int getMaxDamage(net.minecraft.world.item.ItemStack stack) {
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 0);
    }

    /**
     * Whether this stack can take durability damage (has MAX_DAMAGE component).
     */
    default boolean isDamageable(net.minecraft.world.item.ItemStack stack) {
        return stack.has(net.minecraft.core.component.DataComponents.MAX_DAMAGE);
    }

    /**
     * NeoForge's XP repair ratio — default 1.
     */
    default float getXpRepairRatio(net.minecraft.world.item.ItemStack stack) {
        return 1f;
    }

    /**
     * NeoForge's stop-using hook.
     */
    default void onStopUsing(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.entity.LivingEntity entity, int count) {
    }

    /**
     * NeoForge's drop hook — true performs the normal drop.
     */
    default boolean onDroppedByPlayer(net.minecraft.world.item.ItemStack item,
            net.minecraft.world.entity.player.Player player) {
        return true;
    }

    /**
     * Whether this stack is piglin bartering currency (gold ingots).
     */
    default boolean isPiglinCurrency(net.minecraft.world.item.ItemStack stack) {
        return stack.getItem() == net.minecraft.world.entity.monster.piglin.PiglinAi.BARTERING_ITEM;
    }

    /**
     * Whether wearing this stack keeps piglins neutral (golden armor).
     */
    default boolean makesPiglinsNeutral(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.entity.LivingEntity wearer) {
        return stack.getItem() instanceof net.minecraft.world.item.ArmorItem
                && ((net.minecraft.world.item.ArmorItem) stack.getItem()).getMaterial()
                        == net.minecraft.world.item.ArmorMaterials.GOLD;
    }

    /**
     * Remainder left in the crafting grid. Delegates to vanilla's item-level hook.
     */
    default net.minecraft.world.item.ItemStack getCraftingRemainingItem(net.minecraft.world.item.ItemStack itemStack) {
        if (!hasCraftingRemainingItem(itemStack)) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }
        return new net.minecraft.world.item.ItemStack(self().getCraftingRemainingItem());
    }

    /**
     * Whether this item leaves a crafting remainder.
     */
    default boolean hasCraftingRemainingItem(net.minecraft.world.item.ItemStack stack) {
        return self().hasCraftingRemainingItem();
    }

    /**
     * Whether this stack may be equipped into {@code armorType} on the entity.
     */
    default boolean canEquip(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.entity.EquipmentSlot armorType,
            net.minecraft.world.entity.LivingEntity entity) {
        return entity.getEquipmentSlotForItem(stack) == armorType;
    }

    /**
     * Armor texture override — null keeps vanilla behavior.
     */
    default net.minecraft.resources.ResourceLocation getArmorTexture(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.entity.Entity entity, net.minecraft.world.entity.EquipmentSlot slot,
            net.minecraft.world.item.ArmorMaterial.Layer layer, boolean innerModel) {
        return null;
    }

    /**
     * Stack-level enchantment level lookup.
     */
    default int getEnchantmentLevel(net.minecraft.world.item.ItemStack stack,
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment) {
        return stack.getEnchantments().getLevel(enchantment);
    }

    /**
     * All enchantments visible on the stack.
     */
    default net.minecraft.world.item.enchantment.ItemEnchantments getAllEnchantments(
            net.minecraft.world.item.ItemStack stack,
            net.minecraft.core.HolderLookup.RegistryLookup<net.minecraft.world.item.enchantment.Enchantment> lookup) {
        return stack.getEnchantments();
    }

    /**
     * Whether the grindstone may repair this stack — NeoForge default false.
     */
    default boolean canGrindstoneRepair(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    /**
     * Sweep attack box for this stack.
     */
    default net.minecraft.world.phys.AABB getSweepHitBox(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.Entity target) {
        return target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D);
    }

    /**
     * Whether mining speed resets when the stack changes — fixes MC-176559.
     * Compares component maps ignoring the DAMAGE component.
     */
    default boolean shouldCauseBlockBreakReset(net.minecraft.world.item.ItemStack oldStack,
            net.minecraft.world.item.ItemStack newStack) {
        if (!newStack.is(oldStack.getItem()))
            return true;

        if (!newStack.isDamageableItem() || !oldStack.isDamageableItem())
            return !net.minecraft.world.item.ItemStack.isSameItemSameComponents(newStack, oldStack);

        net.minecraft.core.component.DataComponentMap newComponents = newStack.getComponents();
        net.minecraft.core.component.DataComponentMap oldComponents = oldStack.getComponents();

        if (newComponents.isEmpty() || oldComponents.isEmpty())
            return !(newComponents.isEmpty() && oldComponents.isEmpty());

        java.util.Set<net.minecraft.core.component.DataComponentType<?>> newKeys =
                new java.util.HashSet<>(newComponents.keySet());
        java.util.Set<net.minecraft.core.component.DataComponentType<?>> oldKeys =
                new java.util.HashSet<>(oldComponents.keySet());

        newKeys.remove(net.minecraft.core.component.DataComponents.DAMAGE);
        oldKeys.remove(net.minecraft.core.component.DataComponents.DAMAGE);

        if (!newKeys.equals(oldKeys))
            return true;

        return !newKeys.stream().allMatch(key ->
                java.util.Objects.equals(newComponents.get(key), oldComponents.get(key)));
    }

    /**
     * Whether this stack may be enchanted at an enchanting table.
     */
    default boolean isEnchantable(net.minecraft.world.item.ItemStack stack) {
        return stack.isEnchantable();
    }

    /**
     * Whether the enchantments on {@code book} may be applied to this stack.
     */
    default boolean isBookEnchantable(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.item.ItemStack book) {
        return stack.isEnchantable() && book.is(net.minecraft.world.item.Items.ENCHANTED_BOOK);
    }

    /**
     * Left-click attack hook — return true to cancel the attack.
     */
    default boolean onLeftClickEntity(net.minecraft.world.item.ItemStack stack,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.entity.Entity entity) {
        return false;
    }
}
