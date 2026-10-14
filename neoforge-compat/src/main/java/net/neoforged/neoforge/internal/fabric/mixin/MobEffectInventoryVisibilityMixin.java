package net.neoforged.neoforge.internal.fabric.mixin;

import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.ClientExtensionsRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * NeoForge parity: IClientMobEffectExtensions.isVisibleInInventory hides effect
 * icons from the inventory screen.
 */
@Mixin(EffectRenderingInventoryScreen.class)
public abstract class MobEffectInventoryVisibilityMixin {

    @Redirect(method = "renderEffects(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getActiveEffects()Ljava/util/Collection;"))
    private Collection<MobEffectInstance> neoforge$filterInvisibleInventoryEffects(LocalPlayer player) {
        Collection<MobEffectInstance> effects = player.getActiveEffects();
        Collection<MobEffectInstance> filtered = null;
        for (MobEffectInstance instance : effects) {
            if (!ClientExtensionsRegistry.getMobEffectExtension(instance.getEffect().value()).isVisibleInInventory(instance)) {
                if (filtered == null) {
                    filtered = new ArrayList<>(effects);
                }
                filtered.remove(instance);
            }
        }
        return filtered != null ? filtered : effects;
    }
}
