package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ItemDecorator;
import net.neoforged.neoforge.internal.fabric.ItemDecoratorRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Runs registered {@link ItemDecorator}s at the head of
 * {@code renderItemDecorations}; a decorator returning {@code true} replaces
 * the vanilla decoration pass (NeoForge semantics).
 */
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsDecorationsDispatchMixin {
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD"), cancellable = true)
    private void neoforge$customItemDecorations(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        ItemDecorator decorator = ItemDecoratorRegistry.get(stack.getItem());
        if (decorator != null && decorator.render((GuiGraphics) (Object) this, font, stack, x, y)) {
            ci.cancel();
        }
    }
}
