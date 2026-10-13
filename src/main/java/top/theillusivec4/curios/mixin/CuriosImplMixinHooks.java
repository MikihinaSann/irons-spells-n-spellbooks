package top.theillusivec4.curios.mixin;

import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

/**
 * Stand-in for Curios' internal mixin hooks. Consulted by the Trinkets bridge
 * when validating whether a stack can enter a slot. ISS's
 * {@code CurioHooksMixin} injects into {@link #isStackValid} to gate spellbooks
 * out of the generic "curio" slot.
 */
public final class CuriosImplMixinHooks {

    private CuriosImplMixinHooks() {
    }

    public static boolean isStackValid(SlotContext slotContext, ItemStack stack) {
        return !stack.isEmpty();
    }
}
