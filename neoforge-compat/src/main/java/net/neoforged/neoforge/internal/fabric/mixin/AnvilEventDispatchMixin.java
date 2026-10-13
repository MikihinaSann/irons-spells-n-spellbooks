package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link AnvilUpdateEvent} at the top of {@code AnvilMenu#createResult}.
 * When a subscriber provides an output stack, it is installed as the result and
 * the vanilla result computation is skipped (matching NeoForge).
 */
@Mixin(AnvilMenu.class)
public abstract class AnvilEventDispatchMixin extends ItemCombinerMenu {

    @Shadow
    @Nullable
    private String itemName;

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private int repairItemCountCost;

    protected AnvilEventDispatchMixin(@Nullable MenuType<?> type, int containerId, net.minecraft.world.entity.player.Inventory playerInventory,
                                      ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$anvilUpdate(CallbackInfo ci) {
        var event = new AnvilUpdateEvent(this.inputSlots.getItem(0), this.inputSlots.getItem(1),
                this.itemName, 0, this.player);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        if (!event.getOutput().isEmpty()) {
            this.resultSlots.setItem(0, event.getOutput());
            this.cost.set(event.getCost());
            this.repairItemCountCost = event.getMaterialCost();
            this.broadcastChanges();
            ci.cancel();
        }
    }
}
