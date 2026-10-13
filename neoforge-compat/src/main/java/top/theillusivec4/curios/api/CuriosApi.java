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

    public static CuriosHelper getCuriosHelper() {
        return CuriosHelper.INSTANCE;
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
    }
}
