package net.neoforged.neoforge.common.extensions;

import net.minecraft.network.chat.Style;

import java.util.function.UnaryOperator;

/**
 * NeoForge's {@code Rarity.getStyleModifier()} exposed as a duck interface —
 * implemented on {@code net.minecraft.world.item.Rarity} by mixin.
 */
public interface IRarityExtension {

    /**
     * The style operator applied to the item's display name. Vanilla rarities
     * return an operator wrapping their {@code ChatFormatting}; extended
     * rarities return the operator supplied to their enum-extension
     * constructor.
     */
    default UnaryOperator<Style> getStyleModifier() {
        return UnaryOperator.identity();
    }
}
