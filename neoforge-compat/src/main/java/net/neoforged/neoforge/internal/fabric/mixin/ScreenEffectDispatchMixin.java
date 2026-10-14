package net.neoforged.neoforge.internal.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Fires {@link RenderBlockScreenEffectEvent} for each overlay
 * {@code ScreenEffectRenderer} draws — mirroring NeoForge's patch.
 */
@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectDispatchMixin {

    @WrapOperation(method = "renderScreenEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderTex(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private static void neoforgeCompat$blockOverlay(TextureAtlasSprite sprite, PoseStack pose, Operation<Void> original) {
        Player player = Minecraft.getInstance().player;
        RenderBlockScreenEffectEvent event = new RenderBlockScreenEffectEvent(player, pose,
                RenderBlockScreenEffectEvent.OverlayType.BLOCK, null,
                player != null ? player.blockPosition() : null);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            original.call(sprite, pose);
        }
    }

    @WrapOperation(method = "renderScreenEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderWater(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private static void neoforgeCompat$waterOverlay(Minecraft mc, PoseStack pose, Operation<Void> original) {
        RenderBlockScreenEffectEvent event = new RenderBlockScreenEffectEvent(mc.player, pose,
                RenderBlockScreenEffectEvent.OverlayType.WATER, null,
                mc.player != null ? mc.player.blockPosition() : null);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            original.call(mc, pose);
        }
    }

    @WrapOperation(method = "renderScreenEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private static void neoforgeCompat$fireOverlay(Minecraft mc, PoseStack pose, Operation<Void> original) {
        RenderBlockScreenEffectEvent event = new RenderBlockScreenEffectEvent(mc.player, pose,
                RenderBlockScreenEffectEvent.OverlayType.FIRE, null,
                mc.player != null ? mc.player.blockPosition() : null);
        NeoForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            original.call(mc, pose);
        }
    }
}
