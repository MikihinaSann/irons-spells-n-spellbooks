package net.neoforged.neoforge.internal.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fires {@link RenderHandEvent} before each first-person arm render —
 * cancelling skips that hand, matching NeoForge's injection point.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class RenderHandDispatchMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void neoforgeCompat$renderHand(AbstractClientPlayer player, float partialTicks, float pitch,
            InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress,
            PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, CallbackInfo ci) {
        RenderHandEvent event = new RenderHandEvent(hand, poseStack, bufferSource, combinedLight,
                partialTicks, pitch, swingProgress, equippedProgress, stack);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
