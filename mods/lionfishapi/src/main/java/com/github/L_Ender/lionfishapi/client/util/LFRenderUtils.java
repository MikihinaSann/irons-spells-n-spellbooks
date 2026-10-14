package com.github.L_Ender.lionfishapi.client.util;

import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

public class LFRenderUtils {
   public static void matrixStackFromModel(PoseStack matrixStack, AdvancedModelBox AdvancedModelBox) {
      AdvancedModelBox parent = AdvancedModelBox.getParent();
      if (parent != null) {
         matrixStackFromModel(matrixStack, parent);
      }

      AdvancedModelBox.translateAndRotate(matrixStack);
   }

   public static Vec3 matrixStackFromModel(Entity entity, float entityYaw, AdvancedModelBox modelRenderer) {
      PoseStack matrixStack = new PoseStack();
      matrixStack.translate(entity.getX(), entity.getY(), entity.getZ());
      matrixStack.mulPose(new Quaternionf().rotationY((-entityYaw + 180.0F) * (float) (Math.PI / 180.0)));
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.translate(0.0F, -1.5F, 0.0F);
      matrixStackFromModel(matrixStack, modelRenderer);
      Pose matrixEntry = matrixStack.last();
      Matrix4f matrix4f = matrixEntry.pose();
      Vector4f vec = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      vec.mul(matrix4f);
      return new Vec3(vec.x(), vec.y(), vec.z());
   }
}
