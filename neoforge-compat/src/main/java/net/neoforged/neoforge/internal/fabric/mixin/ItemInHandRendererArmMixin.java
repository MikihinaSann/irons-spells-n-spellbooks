package net.neoforged.neoforge.internal.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Dispatches {@link RenderArmEvent} from
 * {@link ItemInHandRenderer#renderPlayerArm} — the first-person arm render.
 * Cancellation suppresses the vanilla arm draw.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererArmMixin {

    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    private void neoforge$renderArm(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight,
            float equippedProgress, float swingProgress, HumanoidArm arm, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        var event = new RenderArmEvent(poseStack, bufferSource, combinedLight, player, arm);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
