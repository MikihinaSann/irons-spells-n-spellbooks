package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

/**
 * Posts {@link RenderTooltipEvent.Color} where vanilla calls
 * {@link TooltipRenderUtil#renderTooltipBackground}; the event's colors feed
 * the 9-arg overload, matching NeoForge's patch.
 */
@Mixin(GuiGraphics.class)
public abstract class RenderTooltipColorMixin {

    /** NeoForge adds this field to GuiGraphics; mixin-added here. Set from
     * the ItemStack tooltip overload before the internal renderer runs. */
    @org.spongepowered.asm.mixin.Unique
    private ItemStack tooltipStack;
    @org.spongepowered.asm.mixin.Unique
    private Font tooltipFont;
    @org.spongepowered.asm.mixin.Unique
    private List<ClientTooltipComponent> tooltipComponents;
    @org.spongepowered.asm.mixin.Unique
    private int tooltipMouseX;
    @org.spongepowered.asm.mixin.Unique
    private int tooltipMouseY;

    @org.spongepowered.asm.mixin.injection.Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V",
            at = @At("HEAD"))
    private void irons_spellbooks$captureTooltipStack(Font font, ItemStack stack, int x, int y,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.tooltipStack = stack;
    }

    /** Capture the tooltip context for the background draw, which vanilla
     * performs inside a drawManaged lambda in a private helper. */
    @org.spongepowered.asm.mixin.injection.Inject(
            method = "renderTooltipInternal",
            at = @At("HEAD"))
    private void irons_spellbooks$captureTooltipContext(Font font, List<ClientTooltipComponent> components,
            int x, int y, ClientTooltipPositioner positioner,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        this.tooltipFont = font;
        this.tooltipComponents = components;
        this.tooltipMouseX = x;
        this.tooltipMouseY = y;
    }

    @WrapOperation(
            method = "method_51743",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"))
    private void irons_spellbooks$colorEvent(GuiGraphics graphics, int x, int y, int width, int height, int z,
            Operation<Void> original) {
        RenderTooltipEvent.Color event = new RenderTooltipEvent.Color(
                tooltipStack, graphics, tooltipMouseX, tooltipMouseY, tooltipFont,
                0xF0100010, 0x505000FF, 0x5028007F,
                tooltipComponents, graphics.pose());
        NeoForge.EVENT_BUS.post(event);
        int bgStart = event.getBackgroundStart();
        int bgEnd = event.getBackgroundEnd();
        int borderStart = event.getBorderStart();
        int borderEnd = event.getBorderEnd();
        int i = x - 3;
        int j = y - 3;
        int k = width + 6;
        int l = height + 6;
        graphics.fill(i, j - 1, i + k, j, z, bgStart);
        graphics.fill(i, j + l, i + k, j + l + 1, z, bgStart);
        graphics.fill(i, j, i + k, j + l, z, bgStart);
        graphics.fill(i - 1, j, i, j + l, z, bgStart);
        graphics.fill(i + k, j, i + k + 1, j + l, z, bgStart);
        graphics.fillGradient(i, j + 1, i + 1, j + l - 1, z, borderStart, borderEnd);
        graphics.fillGradient(i + k - 1, j + 1, i + k, j + l - 1, z, borderStart, borderEnd);
        graphics.fillGradient(i, j + 1, i + k, j + 1, z, borderStart, borderStart);
        graphics.fillGradient(i, j + l - 1, i + k, j + l, z, borderEnd, borderEnd);
    }
}
