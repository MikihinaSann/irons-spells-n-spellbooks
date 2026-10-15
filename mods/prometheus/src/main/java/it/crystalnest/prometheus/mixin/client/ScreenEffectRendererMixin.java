package it.crystalnest.prometheus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ScreenEffectRenderer.class})
public abstract class ScreenEffectRendererMixin {
   @WrapOperation(
      method = {"renderFire"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
      )}
   )
   private static TextureAtlasSprite onRenderFire(Material originalMaterial, Operation<TextureAtlasSprite> original, Minecraft minecraft, PoseStack poseStack) {
      ResourceLocation fireType = minecraft.player != null ? ((FireTyped)minecraft.player).getFireType() : null;
      return FireManager.isRegisteredType(fireType)
         ? FireClientManager.getSprite1(fireType)
         : (TextureAtlasSprite)original.call(new Object[]{originalMaterial});
   }
}
