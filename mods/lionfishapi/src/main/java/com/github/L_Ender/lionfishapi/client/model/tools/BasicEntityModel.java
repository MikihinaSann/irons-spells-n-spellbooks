package com.github.L_Ender.lionfishapi.client.model.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class BasicEntityModel<T extends Entity> extends EntityModel<T> {
   public int textureWidth = 64;
   public int textureHeight = 32;

   protected BasicEntityModel() {
      this(RenderType::entityCutoutNoCull);
   }

   protected BasicEntityModel(Function<ResourceLocation, RenderType> p_102613_) {
      super(p_102613_);
   }

   public void renderToBuffer(PoseStack p_103013_, VertexConsumer p_103014_, int p_103015_, int p_103016_, int p_350603_) {
      this.root().render(p_103013_, p_103014_, p_103015_, p_103016_, p_350603_);
   }

   public abstract BasicModelPart root();

   public abstract void setupAnim(T var1, float var2, float var3, float var4, float var5, float var6);

   public void prepareMobModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
   }
}
