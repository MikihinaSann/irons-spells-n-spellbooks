package net.neoforged.neoforge.internal.fabric.mixin.api;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.extensions.IEnumExtensionCarrier;
import net.neoforged.neoforge.common.extensions.IRarityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.UnaryOperator;

/**
 * NeoForge patches {@code Rarity} with a
 * {@code (int, String, UnaryOperator<Style>)} constructor and a
 * {@code styleModifier} field consumed by item-name styling. Fabric cannot
 * add constructors, so extended constants are created through the vanilla
 * {@code (int, String, ChatFormatting)} constructor with a null color and the
 * style operator stored here via {@link IEnumExtensionCarrier}.
 */
@Mixin(Rarity.class)
public abstract class RarityApiMixin implements IEnumExtensionCarrier, IRarityExtension {

    @Shadow
    private ChatFormatting color;
    // NOTE: vanilla field is `final`; shadow without final for parity-safe reads.

    @Unique
    private UnaryOperator<Style> neoforge$styleModifier;

    /** NeoForge's {@code Rarity.getStyleModifier()}. */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public UnaryOperator<Style> getStyleModifier() {
        UnaryOperator<Style> modifier = neoforge$styleModifier;
        if (modifier == null) {
            ChatFormatting c = color;
            modifier = neoforge$styleModifier = c == null
                    ? UnaryOperator.identity()
                    : style -> ((Style) style).applyFormat(c);
        }
        return modifier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void neoforge$setExtraCtorArg(Class<?> declaredType, Object value) {
        if (declaredType == ChatFormatting.class && value instanceof UnaryOperator) {
            neoforge$styleModifier = (UnaryOperator<Style>) value;
        }
    }
}
