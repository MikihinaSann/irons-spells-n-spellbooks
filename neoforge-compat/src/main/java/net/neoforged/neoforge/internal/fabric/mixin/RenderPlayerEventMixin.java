package net.neoforged.neoforge.internal.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Posts {@link RenderPlayerEvent.Pre}/{@link RenderPlayerEvent.Post} around the player render. */
@Mixin(PlayerRenderer.class)
public abstract class RenderPlayerEventMixin {

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void irons_spellbooks$pre(AbstractClientPlayer player, float entityYaw, float partialTicks,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        RenderPlayerEvent.Pre event = new RenderPlayerEvent.Pre(player,
                (PlayerRenderer) (Object) this, partialTicks, poseStack, buffer, packedLight);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"))
    private void irons_spellbooks$post(AbstractClientPlayer player, float entityYaw, float partialTicks,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new RenderPlayerEvent.Post(player,
                (PlayerRenderer) (Object) this, partialTicks, poseStack, buffer, packedLight));
    }
}
