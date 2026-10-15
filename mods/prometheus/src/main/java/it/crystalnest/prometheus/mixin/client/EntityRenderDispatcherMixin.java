package it.crystalnest.prometheus.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.client.FireClientManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EntityRenderDispatcher.class})
public abstract class EntityRenderDispatcherMixin {
   @WrapOperation(
      method = {"renderFlame"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
         ordinal = 0
      )}
   )
   private TextureAtlasSprite onRenderFlameAtSprite0(
      Material originalMaterial,
      Operation<TextureAtlasSprite> original,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      Entity entity,
      Quaternionf matrix
   ) {
      ResourceLocation fireType = ((FireTyped)entity).getFireType();
      return FireManager.isRegisteredType(fireType)
         ? FireClientManager.getSprite0(fireType)
         : (TextureAtlasSprite)original.call(new Object[]{originalMaterial});
   }

   @WrapOperation(
      method = {"renderFlame"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
         ordinal = 1
      )}
   )
   private TextureAtlasSprite onRenderFlameAtSprite1(
      Material originalMaterial,
      Operation<TextureAtlasSprite> original,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      Entity entity,
      Quaternionf matrix
   ) {
      ResourceLocation fireType = ((FireTyped)entity).getFireType();
      return FireManager.isRegisteredType(fireType)
         ? FireClientManager.getSprite1(fireType)
         : (TextureAtlasSprite)original.call(new Object[]{originalMaterial});
   }
}
