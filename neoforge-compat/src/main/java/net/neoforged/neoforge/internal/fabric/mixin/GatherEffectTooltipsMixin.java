package net.neoforged.neoforge.internal.fabric.mixin;

import java.util.ArrayList;
import java.util.List;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.event.GatherEffectScreenTooltipsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Posts {@link GatherEffectScreenTooltipsEvent} when the effect screen assembles
 * a tooltip for a hovered effect - the vanilla code calls
 * {@code List.of(name, duration)} inside its render loop.
 */
@Mixin(EffectRenderingInventoryScreen.class)
public abstract class GatherEffectTooltipsMixin {

    @WrapOperation(
            method = "renderEffects",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private List<Component> irons_spellbooks$gatherTooltips(Object name, Object duration,
            Operation<List<Component>> original, @Local MobEffectInstance effectInstance) {
        List<Component> tooltip = new ArrayList<>(original.call(name, duration));
        NeoForge.EVENT_BUS.post(new GatherEffectScreenTooltipsEvent(
                (EffectRenderingInventoryScreen<?>) (Object) this, effectInstance, tooltip));
        return tooltip;
    }
}
