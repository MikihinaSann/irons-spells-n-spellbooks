package net.neoforged.neoforge.common.extensions;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Elytra delegation: the stack extension must route to the item extension, and
 * the item baseline must reproduce vanilla ElytraItem.isFlyEnabled behavior —
 * intact elytra flies, broken elytra does not, non-elytra/empty never do at the
 * baseline (attribute/effect overrides live in mod-side interface overrides).
 */
public class ElytraExtensionTest {

    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    @Test
    void vanillaIntactElytraCanFly() {
        assertTrue(new ItemStack(Items.ELYTRA).canElytraFly(null));
    }

    @Test
    void vanillaBrokenElytraCannotFly() {
        ItemStack stack = new ItemStack(Items.ELYTRA);
        stack.setDamageValue(stack.getMaxDamage());
        assertFalse(stack.canElytraFly(null));
    }

    @Test
    void nonElytraAndEmptyStacksBaselineFalse() {
        assertFalse(new ItemStack(Items.DIAMOND_CHESTPLATE).canElytraFly(null));
        // empty stack delegates to Items.AIR's baseline — the Angel Wings /
        // apothic overrides are interface mixins that don't run in unit tests
        assertFalse(ItemStack.EMPTY.canElytraFly(null));
    }

    @Test
    void flightTickBaselinePermitsContinuation() {
        assertTrue(new ItemStack(Items.ELYTRA).elytraFlightTick(null, 1));
        assertTrue(ItemStack.EMPTY.elytraFlightTick(null, 1));
    }
}
