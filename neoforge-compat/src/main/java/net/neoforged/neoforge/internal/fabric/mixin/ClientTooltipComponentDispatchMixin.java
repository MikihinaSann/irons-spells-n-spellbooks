package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.internal.fabric.ClientTooltipFactories;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Consults {@code RegisterClientTooltipComponentFactoriesEvent} registrations
 * before the vanilla instanceof chain in {@code ClientTooltipComponent.create}.
 */
@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentDispatchMixin {

    @Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            at = @At("HEAD"), cancellable = true)
    private static void neoforgeCompat$customTooltip(TooltipComponent component,
            CallbackInfoReturnable<ClientTooltipComponent> cir) {
        ClientTooltipComponent custom = ClientTooltipFactories.create(component);
        if (custom != null) {
            cir.setReturnValue(custom);
        }
    }
}
