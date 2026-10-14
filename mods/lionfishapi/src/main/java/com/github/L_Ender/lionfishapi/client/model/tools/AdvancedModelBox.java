package com.github.L_Ender.lionfishapi.client.model.tools;

import com.github.L_Ender.lionfishapi.client.util.LFRenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public class AdvancedModelBox extends BasicModelPart {
   public float defaultRotationX;
   public float defaultRotationY;
   public float defaultRotationZ;
   public float defaultOffsetX;
   public float defaultOffsetY;
   public float defaultOffsetZ;
   public float defaultPositionX;
   public float defaultPositionY;
   public float defaultPositionZ;
   public float defaultscaleX;
   public float defaultscaleY;
   public float defaultscaleZ;
   public int textureOffsetX;
   public int textureOffsetY;
   public boolean scaleChildren;
   private AdvancedEntityModel model;
   private AdvancedModelBox parent;
   private int displayList;
   private boolean compiled;
   public ObjectList<LionfishModelRenderUtils.ModelBox> cubeList;
   public ObjectList<BasicModelPart> childModels;
   private float textureWidth;
   private float textureHeight;
   public float offsetX;
   public float offsetY;
   public float offsetZ;
   private boolean isHidden = false;
   public String boxName = "";

   public AdvancedModelBox(AdvancedEntityModel model, String name) {
      super(model);
      this.textureWidth = model.texWidth;
      this.textureHeight = model.texHeight;
      this.model = model;
      this.cubeList = new ObjectArrayList();
      this.childModels = new ObjectArrayList();
      this.boxName = name;
   }

   public AdvancedModelBox(AdvancedEntityModel model) {
      this(model, null);
      this.textureWidth = model.texWidth;
      this.textureHeight = model.texHeight;
      this.cubeList = new ObjectArrayList();
      this.childModels = new ObjectArrayList();
   }

   public AdvancedModelBox(AdvancedEntityModel model, int textureOffsetX, int textureOffsetY) {
      this(model);
      this.textureWidth = model.texWidth;
      this.textureHeight = model.texHeight;
      this.setTextureOffset(textureOffsetX, textureOffsetY);
      this.cubeList = new ObjectArrayList();
      this.childModels = new ObjectArrayList();
   }

   public AdvancedModelBox(AdvancedModelBox copyFrom) {
      this(copyFrom.getModel(), copyFrom.textureOffsetX, copyFrom.textureOffsetY);
      this.rotationPointX = copyFrom.rotationPointX;
      this.rotationPointY = copyFrom.rotationPointY;
      this.rotationPointZ = copyFrom.rotationPointZ;
      this.rotateAngleX = copyFrom.rotateAngleX;
      this.rotateAngleY = copyFrom.rotateAngleY;
      this.rotateAngleZ = copyFrom.rotateAngleZ;
      this.xScale = copyFrom.xScale;
      this.yScale = copyFrom.yScale;
      this.zScale = copyFrom.zScale;
      this.defaultPositionX = copyFrom.defaultPositionX;
      this.defaultPositionY = copyFrom.defaultPositionY;
      this.defaultPositionZ = copyFrom.defaultPositionZ;
      this.defaultRotationX = copyFrom.defaultRotationX;
      this.defaultRotationY = copyFrom.defaultRotationY;
      this.defaultRotationZ = copyFrom.defaultRotationZ;
      this.defaultscaleX = copyFrom.defaultscaleX;
      this.defaultscaleY = copyFrom.defaultscaleZ;
      this.defaultscaleZ = copyFrom.defaultscaleZ;
      this.scaleChildren = copyFrom.scaleChildren;
      this.cubeList.addAll(copyFrom.cubeList);
      this.childModels = copyFrom.childModels;
   }

   public BasicModelPart setTexSize(int p_78787_1_, int p_78787_2_) {
      this.textureWidth = p_78787_1_;
      this.textureHeight = p_78787_2_;
      return this;
   }

   @Override
   public BasicModelPart addBox(
      String p_217178_1_,
      float p_217178_2_,
      float p_217178_3_,
      float p_217178_4_,
      int p_217178_5_,
      int p_217178_6_,
      int p_217178_7_,
      float p_217178_8_,
      int p_217178_9_,
      int p_217178_10_
   ) {
      this.setTextureOffset(p_217178_9_, p_217178_10_);
      this.addBox(
         this.textureOffsetX,
         this.textureOffsetY,
         p_217178_2_,
         p_217178_3_,
         p_217178_4_,
         p_217178_5_,
         p_217178_6_,
         p_217178_7_,
         p_217178_8_,
         p_217178_8_,
         p_217178_8_,
         this.mirror,
         false
      );
      return this;
   }

   @Override
   public BasicModelPart addBox(float p_228300_1_, float p_228300_2_, float p_228300_3_, float p_228300_4_, float p_228300_5_, float p_228300_6_) {
      this.addBox(
         this.textureOffsetX,
         this.textureOffsetY,
         p_228300_1_,
         p_228300_2_,
         p_228300_3_,
         p_228300_4_,
         p_228300_5_,
         p_228300_6_,
         0.0F,
         0.0F,
         0.0F,
         this.mirror,
         false
      );
      return this;
   }

   @Override
   public BasicModelPart addBox(
      float p_228304_1_, float p_228304_2_, float p_228304_3_, float p_228304_4_, float p_228304_5_, float p_228304_6_, boolean p_228304_7_
   ) {
      this.addBox(
         this.textureOffsetX,
         this.textureOffsetY,
         p_228304_1_,
         p_228304_2_,
         p_228304_3_,
         p_228304_4_,
         p_228304_5_,
         p_228304_6_,
         0.0F,
         0.0F,
         0.0F,
         p_228304_7_,
         false
      );
      return this;
   }

   @Override
   public void addBox(float p_228301_1_, float p_228301_2_, float p_228301_3_, float p_228301_4_, float p_228301_5_, float p_228301_6_, float p_228301_7_) {
      this.addBox(
         this.textureOffsetX,
         this.textureOffsetY,
         p_228301_1_,
         p_228301_2_,
         p_228301_3_,
         p_228301_4_,
         p_228301_5_,
         p_228301_6_,
         p_228301_7_,
         p_228301_7_,
         p_228301_7_,
         this.mirror,
         false
      );
   }

   @Override
   public void addBox(
      float p_228302_1_,
      float p_228302_2_,
      float p_228302_3_,
      float p_228302_4_,
      float p_228302_5_,
      float p_228302_6_,
      float p_228302_7_,
      float p_228302_8_,
      float p_228302_9_
   ) {
      this.addBox(
         this.textureOffsetX,
         this.textureOffsetY,
         p_228302_1_,
         p_228302_2_,
         p_228302_3_,
         p_228302_4_,
         p_228302_5_,
         p_228302_6_,
         p_228302_7_,
         p_228302_8_,
         p_228302_9_,
         this.mirror,
         false
      );
   }

   @Override
   public void addBox(
      float p_228303_1_, float p_228303_2_, float p_228303_3_, float p_228303_4_, float p_228303_5_, float p_228303_6_, float p_228303_7_, boolean p_228303_8_
   ) {
      this.addBox(
         this.textureOffsetX,
         this.textureOffsetY,
         p_228303_1_,
         p_228303_2_,
         p_228303_3_,
         p_228303_4_,
         p_228303_5_,
         p_228303_6_,
         p_228303_7_,
         p_228303_7_,
         p_228303_7_,
         p_228303_8_,
         false
      );
   }

   private void addBox(
      int p_228305_1_,
      int p_228305_2_,
      float p_228305_3_,
      float p_228305_4_,
      float p_228305_5_,
      float p_228305_6_,
      float p_228305_7_,
      float p_228305_8_,
      float p_228305_9_,
      float p_228305_10_,
      float p_228305_11_,
      boolean p_228305_12_,
      boolean p_228305_13_
   ) {
      this.cubeList
         .add(
            new LionfishModelRenderUtils.ModelBox(
               p_228305_1_,
               p_228305_2_,
               p_228305_3_,
               p_228305_4_,
               p_228305_5_,
               p_228305_6_,
               p_228305_7_,
               p_228305_8_,
               p_228305_9_,
               p_228305_10_,
               p_228305_11_,
               p_228305_12_,
               this.textureWidth,
               this.textureHeight
            )
         );
   }

   public void setShouldScaleChildren(boolean scaleChildren) {
      this.scaleChildren = scaleChildren;
   }

   public void setScale(float xScale, float yScale, float zScale) {
      this.xScale = xScale;
      this.yScale = yScale;
      this.zScale = zScale;
   }

   public void setScaleX(float xScale) {
      this.xScale = xScale;
   }

   public void setScaleY(float yScale) {
      this.yScale = yScale;
   }

   public void setScaleZ(float zScale) {
      this.zScale = zScale;
   }

   public void setIsHidden(boolean isHidden) {
      this.isHidden = isHidden;
   }

   public boolean isHidden() {
      return this.isHidden;
   }

   public void updateDefaultPose() {
      this.defaultRotationX = this.rotateAngleX;
      this.defaultRotationY = this.rotateAngleY;
      this.defaultRotationZ = this.rotateAngleZ;
      this.defaultPositionX = this.rotationPointX;
      this.defaultPositionY = this.rotationPointY;
      this.defaultPositionZ = this.rotationPointZ;
      this.defaultscaleX = this.xScale;
      this.defaultscaleY = this.yScale;
      this.defaultscaleZ = this.zScale;
   }

   public void setPos(float xIn, float yIn, float zIn) {
      this.rotationPointX = xIn;
      this.rotationPointY = yIn;
      this.rotationPointZ = zIn;
   }

   public void offsetPos(Vector3f p_253873_) {
      this.rotationPointX = this.rotationPointX + p_253873_.x();
      this.rotationPointY = this.rotationPointY + p_253873_.y();
      this.rotationPointZ = this.rotationPointZ + p_253873_.z();
   }

   public void offsetRotation(Vector3f p_253983_) {
      this.rotateAngleX = this.rotateAngleX + p_253983_.x();
      this.rotateAngleY = this.rotateAngleY + p_253983_.y();
      this.rotateAngleZ = this.rotateAngleZ + p_253983_.z();
   }

   public void offsetScale(Vector3f p_253957_) {
      this.xScale = this.xScale + p_253957_.x();
      this.yScale = this.yScale + p_253957_.y();
      this.zScale = this.zScale + p_253957_.z();
   }

   public void resetToDefaultPose() {
      this.rotateAngleX = this.defaultRotationX;
      this.rotateAngleY = this.defaultRotationY;
      this.rotateAngleZ = this.defaultRotationZ;
      this.rotationPointX = this.defaultPositionX;
      this.rotationPointY = this.defaultPositionY;
      this.rotationPointZ = this.defaultPositionZ;
      this.xScale = this.defaultscaleX;
      this.yScale = this.defaultscaleY;
      this.zScale = this.defaultscaleZ;
   }

   @Override
   public void addChild(BasicModelPart child) {
      super.addChild(child);
      this.childModels.add(child);
      if (child instanceof AdvancedModelBox advancedChild) {
         advancedChild.setParent(this);
      }
   }

   public AdvancedModelBox getParent() {
      return this.parent;
   }

   public void setParent(AdvancedModelBox parent) {
      this.parent = parent;
   }

   public void parentedPostRender(float scale) {
      if (this.parent != null) {
         this.parent.parentedPostRender(scale);
      }
   }

   public void renderWithParents(float scale) {
      if (this.parent != null) {
         this.parent.renderWithParents(scale);
      }
   }

   @Override
   public void translateAndRotate(PoseStack matrixStackIn) {
      matrixStackIn.translate(this.rotationPointX / 16.0F, this.rotationPointY / 16.0F, this.rotationPointZ / 16.0F);
      if (this.rotateAngleZ != 0.0F) {
         matrixStackIn.mulPose(Axis.ZP.rotation(this.rotateAngleZ));
      }

      if (this.rotateAngleY != 0.0F) {
         matrixStackIn.mulPose(Axis.YP.rotation(this.rotateAngleY));
      }

      if (this.rotateAngleX != 0.0F) {
         matrixStackIn.mulPose(Axis.XP.rotation(this.rotateAngleX));
      }

      matrixStackIn.scale(this.xScale, this.yScale, this.zScale);
   }

   @Override
   public void render(PoseStack p_228309_1_, VertexConsumer p_228309_2_, int packedLightIn, int packedOverlayIn, int alpha) {
      if (this.showModel && (!this.cubeList.isEmpty() || !this.childModels.isEmpty())) {
         p_228309_1_.pushPose();
         this.translateAndRotate(p_228309_1_);
         this.doRender(p_228309_1_.last(), p_228309_2_, packedLightIn, packedOverlayIn, alpha);
         ObjectListIterator var9 = this.childModels.iterator();
         if (!this.scaleChildren) {
            p_228309_1_.scale(1.0F / Math.max(this.xScale, 1.0E-4F), 1.0F / Math.max(this.yScale, 1.0E-4F), 1.0F / Math.max(this.zScale, 1.0E-4F));
         }

         while (var9.hasNext()) {
            BasicModelPart lvt_10_1_ = (BasicModelPart)var9.next();
            lvt_10_1_.render(p_228309_1_, p_228309_2_, packedLightIn, packedOverlayIn, alpha);
         }

         p_228309_1_.popPose();
      }
   }

   private void doRender(Pose matrixEntryIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int alpha) {
      Matrix4f lvt_9_1_ = matrixEntryIn.pose();
      Matrix3f lvt_10_1_ = matrixEntryIn.normal();
      ObjectListIterator var11 = this.cubeList.iterator();

      while (var11.hasNext()) {
         LionfishModelRenderUtils.ModelBox lvt_12_1_ = (LionfishModelRenderUtils.ModelBox)var11.next();

         for (LionfishModelRenderUtils.TexturedQuad lvt_16_1_ : lvt_12_1_.quads) {
            Vector3f vector3f = new Vector3f(lvt_16_1_.normal);
            vector3f.mul(lvt_10_1_);
            float f = vector3f.x();
            float f1 = vector3f.y();
            float f2 = vector3f.z();

            for (int lvt_21_1_ = 0; lvt_21_1_ < 4; lvt_21_1_++) {
               LionfishModelRenderUtils.PositionTextureVertex lvt_22_1_ = lvt_16_1_.vertexPositions[lvt_21_1_];
               float lvt_23_1_ = lvt_22_1_.position.x() / 16.0F;
               float lvt_24_1_ = lvt_22_1_.position.y() / 16.0F;
               float lvt_25_1_ = lvt_22_1_.position.z() / 16.0F;
               Vector4f vector4f = new Vector4f(lvt_23_1_, lvt_24_1_, lvt_25_1_, 1.0F);
               vector4f.mul(lvt_9_1_);
               bufferIn.addVertex(
                  vector4f.x(), vector4f.y(), vector4f.z(), alpha, lvt_22_1_.textureU, lvt_22_1_.textureV, packedOverlayIn, packedLightIn, f, f1, f2
               );
            }
         }
      }
   }

   public AdvancedEntityModel getModel() {
      return this.model;
   }

   private float calculateRotation(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
      float movementScale = this.model.getMovementScale();
      float rotation = Mth.cos(f * (speed * movementScale) + offset) * (degree * movementScale) * f1 + weight * f1;
      return invert ? -rotation : rotation;
   }

   public void walk(float speed, float degree, boolean invert, float offset, float weight, float walk, float walkAmount) {
      this.rotateAngleX = this.rotateAngleX + this.calculateRotation(speed, degree, invert, offset, weight, walk, walkAmount);
   }

   public void flap(float speed, float degree, boolean invert, float offset, float weight, float flap, float flapAmount) {
      this.rotateAngleZ = this.rotateAngleZ + this.calculateRotation(speed, degree, invert, offset, weight, flap, flapAmount);
   }

   public void swing(float speed, float degree, boolean invert, float offset, float weight, float swing, float swingAmount) {
      this.rotateAngleY = this.rotateAngleY + this.calculateRotation(speed, degree, invert, offset, weight, swing, swingAmount);
   }

   public void bob(float speed, float degree, boolean bounce, float f, float f1) {
      float movementScale = this.model.getMovementScale();
      degree *= movementScale;
      speed *= movementScale;
      float bob = (float)(Math.sin(f * speed) * f1 * degree - f1 * degree);
      if (bounce) {
         bob = (float)(-Math.abs(Math.sin(f * speed) * f1 * degree));
      }

      this.rotationPointY += bob;
   }

   public AdvancedModelBox setTextureOffset(int textureOffsetX, int textureOffsetY) {
      this.textureOffsetX = textureOffsetX;
      this.textureOffsetY = textureOffsetY;
      return this;
   }

   public void transitionTo(AdvancedModelBox to, float timer, float maxTime) {
      this.rotateAngleX = this.rotateAngleX + (to.rotateAngleX - this.rotateAngleX) / maxTime * timer;
      this.rotateAngleY = this.rotateAngleY + (to.rotateAngleY - this.rotateAngleY) / maxTime * timer;
      this.rotateAngleZ = this.rotateAngleZ + (to.rotateAngleZ - this.rotateAngleZ) / maxTime * timer;
      this.rotationPointX = this.rotationPointX + (to.rotationPointX - this.rotationPointX) / maxTime * timer;
      this.rotationPointY = this.rotationPointY + (to.rotationPointY - this.rotationPointY) / maxTime * timer;
      this.rotationPointZ = this.rotationPointZ + (to.rotationPointZ - this.rotationPointZ) / maxTime * timer;
      this.offsetX = this.offsetX + (to.offsetX - this.offsetX) / maxTime * timer;
      this.offsetY = this.offsetY + (to.offsetY - this.offsetY) / maxTime * timer;
      this.offsetZ = this.offsetZ + (to.offsetZ - this.offsetZ) / maxTime * timer;
   }

   public Vec3 getWorldPos(Entity entity, float delta) {
      PoseStack matrixStack = new PoseStack();
      float dx = (float)(entity.xOld + (entity.getX() - entity.xOld) * delta);
      float dy = (float)(entity.yOld + (entity.getY() - entity.yOld) * delta);
      float dz = (float)(entity.zOld + (entity.getZ() - entity.zOld) * delta);
      matrixStack.translate(dx, dy, dz);
      float dYaw = Mth.rotLerp(delta, entity.yRotO, entity.getYRot());
      matrixStack.mulPose(this.quatFromRotationXYZ(0.0F, -dYaw + 180.0F, 0.0F, true));
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.translate(0.0F, -1.5F, 0.0F);
      LFRenderUtils.matrixStackFromModel(matrixStack, this);
      Pose matrixEntry = matrixStack.last();
      Matrix4f matrix4f = matrixEntry.pose();
      Vector4f vec = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
      vec.mul(matrix4f);
      return new Vec3(vec.x(), vec.y(), vec.z());
   }

   public void setWorldPos(Entity entity, Vec3 worldPos, float delta) {
      PoseStack matrixStack = new PoseStack();
      float dx = (float)(entity.xOld + (entity.getX() - entity.xOld) * delta);
      float dy = (float)(entity.yOld + (entity.getY() - entity.yOld) * delta);
      float dz = (float)(entity.zOld + (entity.getZ() - entity.zOld) * delta);
      matrixStack.translate(dx, dy, dz);
      float dYaw = Mth.rotLerp(delta, entity.yRotO, entity.getYRot());
      matrixStack.mulPose(this.quatFromRotationXYZ(0.0F, -dYaw + 180.0F, 0.0F, true));
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.translate(0.0F, -1.5F, 0.0F);
      Pose matrixEntry = matrixStack.last();
      Matrix4f matrix4f = matrixEntry.pose();
      matrix4f.invert();
      Vector4f vec = new Vector4f((float)worldPos.x(), (float)worldPos.y(), (float)worldPos.z(), 1.0F);
      vec.mul(matrix4f);
      this.rotationPointX = vec.x() * 16.0F;
      this.rotationPointY = vec.y() * 16.0F;
      this.rotationPointZ = vec.z() * 16.0F;
   }

   private Quaternionf quatFromRotationXYZ(float x, float y, float z, boolean degrees) {
      if (degrees) {
         x *= (float) (Math.PI / 180.0);
         y *= (float) (Math.PI / 180.0);
         z *= (float) (Math.PI / 180.0);
      }

      return new Quaternionf().rotationXYZ(x, y, z);
   }
}
