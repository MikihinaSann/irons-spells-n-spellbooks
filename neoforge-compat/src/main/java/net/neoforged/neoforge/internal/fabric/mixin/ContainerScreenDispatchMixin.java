package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts {@link ContainerScreenEvent.Render.Background/Foreground}. */
@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenDispatchMixin {
    @Inject(method = "renderBackground", at = @At("TAIL"))
    private void neoforge$containerBackground(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                              float partialTick, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Background(
                (AbstractContainerScreen<?>) (Object) this, guiGraphics, mouseX, mouseY));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void neoforge$containerForeground(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                              float partialTick, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(
                (AbstractContainerScreen<?>) (Object) this, guiGraphics, mouseX, mouseY));
    }
}
