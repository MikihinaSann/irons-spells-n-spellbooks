package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Extended enum rarities (alexscaves:*, cataclysm:*) have a null vanilla color;
 * NeoForge-patched call sites use the style modifier instead, but unpatched
 * sites still call {@code withStyle(rarity.color())}. Treat a null formatting
 * as a no-op instead of crashing in {@code applyFormat}.
 */
@Mixin(Style.class)
public abstract class StyleApplyFormatMixin {

    @Inject(method = "applyFormat", at = @At("HEAD"), cancellable = true)
    private void neoforge_compat$nullSafeFormat(ChatFormatting formatting,
            CallbackInfoReturnable<Style> cir) {
        if (formatting == null) {
            cir.setReturnValue((Style) (Object) this);
        }
    }
}
