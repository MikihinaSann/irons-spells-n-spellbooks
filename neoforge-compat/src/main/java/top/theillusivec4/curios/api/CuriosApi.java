package top.theillusivec4.curios.api;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.capability.TrinketsBackedItemHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * CuriosApi implemented over Trinkets.
 */
public final class CuriosApi {

    private CuriosApi() {
    }

    public static Optional<ICuriosItemHandler> getCuriosInventory(@Nullable LivingEntity entity) {
        if (entity == null) {
            return Optional.empty();
        }
        return TrinketsApi.getTrinketComponent(entity)
                .map(component -> new TrinketsBackedItemHandler(component, entity));
    }

    public static Optional<TrinketComponent> getTrinketComponent(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity);
    }

    /** Curios' slot id for attribute keys: a stable ResourceLocation per slot+index. */
    public static net.minecraft.resources.ResourceLocation getSlotId(SlotContext context) {
        return net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                "curios", (context.identifier() + "_" + context.index()).replace('/', '_'));
    }

    /** Curios' broadcastCurioBreakEvent - fires onCurioBreak on the equipped item. */
    public static void broadcastCurioBreakEvent(SlotContext slotContext) {
        if (!(slotContext.entity() instanceof net.minecraft.world.entity.LivingEntity entity)) {
            return;
        }
        getCuriosInventory(entity).ifPresent(handler -> {
            var stacks = handler.getCurios().get(slotContext.identifier());
            if (stacks != null) {
                ItemStack stack = stacks.getStacks().getStackInSlot(slotContext.index());
                if (!stack.isEmpty()) {
                    invokeBreak(stack, slotContext);
                }
            }
        });
    }

    private static boolean invokeBreak(ItemStack stack, SlotContext slotContext) {
        if (stack.getItem() instanceof top.theillusivec4.curios.api.type.capability.ICurioItem curio) {
            curio.getCurio(stack).onCurioBreak(stack, slotContext);
            return true;
        }
        return false;
    }

    /** Curios' getCurio - the curio capability for an item stack. */
    public static Optional<top.theillusivec4.curios.api.type.capability.ICurio> getCurio(ItemStack stack) {
        if (stack.getItem() instanceof top.theillusivec4.curios.api.type.capability.ICurioItem curio) {
            return Optional.of(curio.getCurio(stack));
        }
        return Optional.empty();
    }

    /** Attribute modifiers the item grants while equipped in the curio slot. */
    public static com.google.common.collect.Multimap<net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeModifier> getAttributeModifiers(
            SlotContext slotContext, net.minecraft.resources.ResourceLocation id, ItemStack stack) {
        if (stack.getItem() instanceof top.theillusivec4.curios.api.type.capability.ICurioItem curio) {
            return curio.getAttributeModifiers(slotContext, id, stack);
        }
        return com.google.common.collect.ImmutableMultimap.of();
    }

    /**
     * Adds a slot modifier to the given attribute map — Curios semantics:
     * wraps the slot identifier in a {@link SlotAttribute}.
     */
    public static void addSlotModifier(
            com.google.common.collect.Multimap<net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>,
                    net.minecraft.world.entity.ai.attributes.AttributeModifier> map,
            String identifier, net.minecraft.resources.ResourceLocation id, double amount,
            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation operation) {
        map.put(SlotAttribute.getOrCreate(identifier),
                new net.minecraft.world.entity.ai.attributes.AttributeModifier(id, amount, operation));
    }

    public static CuriosHelper getCuriosHelper() {
        return CuriosHelper.INSTANCE;
    }

    /**
     * Curios' {@code getEntitySlots}: every slot identifier this entity exposes,
     * mapped to a light {@link ISlotType} descriptor (Trinkets group/slot
     * inventory layout flattened to Curios identifiers).
     */
    public static java.util.Map<String, top.theillusivec4.curios.api.type.ISlotType> getEntitySlots(LivingEntity entity) {
        java.util.Map<String, top.theillusivec4.curios.api.type.ISlotType> slots = new java.util.HashMap<>();
        getTrinketComponent(entity).ifPresent(component ->
                component.getInventory().forEach((group, groupSlots) ->
                        groupSlots.forEach((slot, inv) -> {
                            String id = group + "/" + slot;
                            slots.put(id, new top.theillusivec4.curios.api.type.ISlotType() {
                                @Override public String getIdentifier() { return id; }
                                @Override public int getSize() { return inv.getContainerSize(); }
                                @Override public net.minecraft.resources.ResourceLocation getIcon() {
                                    return net.minecraft.resources.ResourceLocation.withDefaultNamespace("item/empty_armor_slot");
                                }
                            });
                            // Curios flattens "group/slot" to just "slot" when unique
                            slots.putIfAbsent(slot, slots.get(id));
                        })));
        return slots;
    }

    public static class CuriosHelper {
        static final CuriosHelper INSTANCE = new CuriosHelper();

        /**
         * Slot identifiers (Curios style — trinkets {@code group/name}) the item can
         * go into, based on the item's trinkets slot tags.
         */
        public Set<String> getCurioTags(Item item) {
            Set<String> slots = new java.util.HashSet<>();
            var holder = item.builtInRegistryHolder();
            holder.tags().forEach(tag -> {
                var loc = tag.location();
                if ("trinkets".equals(loc.getNamespace())) {
                    String path = loc.getPath();
                    int slash = path.indexOf('/');
                    if (slash > 0) {
                        slots.add(path.substring(slash + 1));
                    }
                }
            });
            return slots;
        }

        /**
         * Curios API: the entity's curio inventory, empty when it has no
         * trinket component.
         */
        public java.util.Optional<top.theillusivec4.curios.api.type.capability.ICuriosItemHandler> getCuriosHandler(
                net.minecraft.world.entity.LivingEntity entity) {
            return getCuriosInventory(entity);
        }

        /** Curios API: first curio matching the predicate. */
        public java.util.Optional<SlotResult> findFirstCurio(
                net.minecraft.world.entity.LivingEntity entity,
                java.util.function.Predicate<ItemStack> filter) {
            return getCuriosInventory(entity).flatMap(inv -> inv.findFirstCurio(filter));
        }

        /** Curios API: first curio matching the item. */
        public java.util.Optional<SlotResult> findFirstCurio(
                net.minecraft.world.entity.LivingEntity entity, Item item) {
            return getCuriosInventory(entity).flatMap(inv -> inv.findFirstCurio(item));
        }

        /** Curios API: all curios matching the predicate. */
        public java.util.List<SlotResult> findCurios(
                net.minecraft.world.entity.LivingEntity entity,
                java.util.function.Predicate<ItemStack> filter) {
            return getCuriosInventory(entity)
                    .map(inv -> inv.findCurios(filter))
                    .orElse(java.util.List.of());
        }

        /** Curios API: all curios matching the item. */
        public java.util.List<SlotResult> findCurios(
                net.minecraft.world.entity.LivingEntity entity, Item item) {
            return getCuriosInventory(entity)
                    .map(inv -> inv.findCurios(item))
                    .orElse(java.util.List.of());
        }

        /** Curios API legacy name: first equipped curio of the item type. */
        public java.util.Optional<SlotResult> findEquippedCurio(
                Item item, net.minecraft.world.entity.LivingEntity entity) {
            return findFirstCurio(entity, item);
        }
    }
}
