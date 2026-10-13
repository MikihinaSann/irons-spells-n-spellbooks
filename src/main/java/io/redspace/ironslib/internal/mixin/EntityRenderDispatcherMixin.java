package io.redspace.ironslib.internal.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import io.redspace.ironslib.transmog.client.TransmogClientHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityRenderDispatcher.class})
public abstract class EntityRenderDispatcherMixin<E extends Entity> {
   @WrapMethod(
      method = {"render"}
   )
   public void irons_spellbooks$handleTransmog(
      E entity,
      double x,
      double y,
      double z,
      float rotationYaw,
      float partialTicks,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      Operation<Void> original
   ) {
      TransmogClientHandler.setTransmogRenderActive(true);
      original.call(new Object[]{entity, x, y, z, rotationYaw, partialTicks, poseStack, buffer, packedLight});
      TransmogClientHandler.setTransmogRenderActive(false);
   }
}
