package top.theillusivec4.curios.api.type.capability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Entity-level curio inventory view, backed by Trinkets' TrinketComponent.
 */
public interface ICuriosItemHandler {

    LivingEntity getWearer();

    /** All equipped curio stacks matching the predicate. */
    List<SlotResult> findCurios(Predicate<ItemStack> predicate);

    default List<SlotResult> findCurios(Item item) {
        return findCurios(stack -> stack.is(item));
    }

    default Optional<SlotResult> findFirstCurio(Item item) {
        return findCurios(item).stream().findFirst();
    }

    default Optional<SlotResult> findFirstCurio(Predicate<ItemStack> predicate) {
        return findCurios(predicate).stream().findFirst();
    }

    /** The stack at {@code identifier[index]}. */
    Optional<SlotResult> findCurio(String identifier, int index);

    /** All stacks in a slot type. */
    List<SlotResult> findCurios(String identifier);

    /** Whether the entity currently wears a matching stack. */
    default boolean isEquipped(Predicate<ItemStack> predicate) {
        return !findCurios(predicate).isEmpty();
    }

    default boolean isEquipped(Item item) {
        return isEquipped(stack -> stack.is(item));
    }

    /** Equips a stack into a slot (mutates the backing inventory). */
    boolean setEquippedCurio(String identifier, int index, ItemStack stack);

    /** All slot identifiers present on the wearer. */
    java.util.Set<String> getSlotIdentifiers();

    /** Flat item-handler view across every equipped curio stack. */
    net.neoforged.neoforge.items.IItemHandlerModifiable getEquippedCurios();
}
