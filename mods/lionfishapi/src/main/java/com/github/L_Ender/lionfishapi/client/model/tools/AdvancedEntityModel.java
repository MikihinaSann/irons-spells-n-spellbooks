package com.github.L_Ender.lionfishapi.client.model.tools;

import com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations.AdvancedAnimationDefinition;
import com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations.AdvancedKeyframeAnimations;
import com.github.L_Ender.lionfishapi.client.model.Animations.ModelAnimator;
import com.github.L_Ender.lionfishapi.client.model.container.TextureOffset;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public abstract class AdvancedEntityModel<T extends Entity> extends BasicEntityModel<T> {
   private float movementScale = 1.0F;
   private final Map<String, TextureOffset> modelTextureMap = Maps.newHashMap();
   private final Map<String, Optional<AdvancedModelBox>> descendantCache = new HashMap<>();
   private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();
   public int texWidth = 32;
   public int texHeight = 32;

   public void updateDefaultPose() {
      this.getAllParts().forEach(modelRenderer -> modelRenderer.updateDefaultPose());
   }

   protected void setTextureOffset(String partName, int x, int y) {
      this.modelTextureMap.put(partName, new TextureOffset(x, y));
   }

   public TextureOffset getTextureOffset(String partName) {
      return this.modelTextureMap.get(partName);
   }

   public void resetToDefaultPose() {
      this.getAllParts().forEach(modelRenderer -> {
         AdvancedModelBox advancedRendererModel = modelRenderer;
         advancedRendererModel.resetToDefaultPose();
      });
   }

   public void faceTarget(float yaw, float pitch, float rotationDivisor, AdvancedModelBox... boxes) {
      float actualRotationDivisor = rotationDivisor * boxes.length;
      float yawAmount = yaw / (180.0F / (float)Math.PI) / actualRotationDivisor;
      float pitchAmount = pitch / (180.0F / (float)Math.PI) / actualRotationDivisor;

      for (AdvancedModelBox box : boxes) {
         box.rotateAngleY += yawAmount;
         box.rotateAngleX += pitchAmount;
      }
   }

   public void chainSwing(AdvancedModelBox[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
      float offset = this.calculateChainOffset(rootOffset, boxes);

      for (int index = 0; index < boxes.length; index++) {
         boxes[index].rotateAngleY = boxes[index].rotateAngleY + this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
      }
   }

   public void chainWave(AdvancedModelBox[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
      float offset = this.calculateChainOffset(rootOffset, boxes);

      for (int index = 0; index < boxes.length; index++) {
         boxes[index].rotateAngleX = boxes[index].rotateAngleX + this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
      }
   }

   public void chainFlap(AdvancedModelBox[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
      float offset = this.calculateChainOffset(rootOffset, boxes);

      for (int index = 0; index < boxes.length; index++) {
         boxes[index].rotateAngleZ = boxes[index].rotateAngleZ + this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
      }
   }

   private float calculateChainRotation(float speed, float degree, float swing, float swingAmount, float offset, int boxIndex) {
      return Mth.cos(swing * (speed * this.movementScale) + offset * boxIndex) * swingAmount * (degree * this.movementScale);
   }

   private float calculateChainOffset(double rootOffset, AdvancedModelBox... boxes) {
      return (float)(rootOffset * Math.PI / (2 * boxes.length));
   }

   public float getMovementScale() {
      return this.movementScale;
   }

   public void setMovementScale(float movementScale) {
      this.movementScale = movementScale;
   }

   public void walk(AdvancedModelBox box, float speed, float degree, boolean invert, float offset, float weight, float walk, float walkAmount) {
      box.walk(speed, degree, invert, offset, weight, walk, walkAmount);
   }

   public void flap(AdvancedModelBox box, float speed, float degree, boolean invert, float offset, float weight, float flap, float flapAmount) {
      box.flap(speed, degree, invert, offset, weight, flap, flapAmount);
   }

   public void swing(AdvancedModelBox box, float speed, float degree, boolean invert, float offset, float weight, float swing, float swingAmount) {
      box.swing(speed, degree, invert, offset, weight, swing, swingAmount);
   }

   public void bob(AdvancedModelBox box, float speed, float degree, boolean bounce, float f, float f1) {
      box.bob(speed, degree, bounce, f, f1);
   }

   public float moveBox(float speed, float degree, boolean bounce, float f, float f1) {
      return bounce ? -Mth.abs(Mth.sin(f * speed) * f1 * degree) : Mth.sin(f * speed) * f1 * degree - f1 * degree;
   }

   public void setRotateAngle(AdvancedModelBox model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
   }

   public void rotate(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
      animator.rotate(model, (float)Math.toRadians(x), (float)Math.toRadians(y), (float)Math.toRadians(z));
   }

   public void rotateMinus(ModelAnimator animator, AdvancedModelBox model, float x, float y, float z) {
      animator.rotate(
         model,
         (float)Math.toRadians(x) - model.defaultRotationX,
         (float)Math.toRadians(y) - model.defaultRotationY,
         (float)Math.toRadians(z) - model.defaultRotationZ
      );
   }

   public void progressRotation(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ, float divisor) {
      model.rotateAngleX = model.rotateAngleX + progress * (rotX - model.defaultRotationX) / divisor;
      model.rotateAngleY = model.rotateAngleY + progress * (rotY - model.defaultRotationY) / divisor;
      model.rotateAngleZ = model.rotateAngleZ + progress * (rotZ - model.defaultRotationZ) / divisor;
   }

   public void progressRotationPrev(AdvancedModelBox model, float progress, float rotX, float rotY, float rotZ, float divisor) {
      model.rotateAngleX += progress * rotX / divisor;
      model.rotateAngleY += progress * rotY / divisor;
      model.rotateAngleZ += progress * rotZ / divisor;
   }

   public void progressPosition(AdvancedModelBox model, float progress, float x, float y, float z, float divisor) {
      model.rotationPointX = model.rotationPointX + progress * (x - model.defaultPositionX) / divisor;
      model.rotationPointY = model.rotationPointY + progress * (y - model.defaultPositionY) / divisor;
      model.rotationPointZ = model.rotationPointZ + progress * (z - model.defaultPositionZ) / divisor;
   }

   public void progressPositionPrev(AdvancedModelBox model, float progress, float x, float y, float z, float divisor) {
      model.rotationPointX += progress * x / divisor;
      model.rotationPointY += progress * y / divisor;
      model.rotationPointZ += progress * z / divisor;
   }

   public abstract Iterable<AdvancedModelBox> getAllParts();

   public Optional<AdvancedModelBox> getAnyDescendantWithName(String key) {
      return this.descendantCache
         .computeIfAbsent(
            key, name -> StreamSupport.stream(this.getAllParts().spliterator(), false).filter(box -> Objects.equals(box.boxName, name)).findFirst()
         );
   }

   public void clearDescendantCache() {
      this.descendantCache.clear();
   }

   protected void animate(AnimationState p_233382_, AdvancedAnimationDefinition p_233383_, float p_233384_) {
      this.animate(p_233382_, p_233383_, p_233384_, 1.0F);
   }

   protected void animateWalk(AdvancedAnimationDefinition p_268159_, float p_268057_, float p_268347_, float p_268138_, float p_268165_) {
      long i = (long)(p_268057_ * 50.0F * p_268138_);
      float f = Math.min(p_268347_ * p_268165_, 1.0F);
      AdvancedKeyframeAnimations.animate(this, p_268159_, i, f, ANIMATION_VECTOR_CACHE);
   }

   protected void animate(AnimationState p_233386_, AdvancedAnimationDefinition p_233387_, float p_233388_, float p_233389_) {
      p_233386_.updateTime(p_233388_, p_233389_);
      p_233386_.ifStarted(p_233392_ -> AdvancedKeyframeAnimations.animate(this, p_233387_, p_233392_.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
   }

   protected void applyStatic(AdvancedAnimationDefinition p_288996_) {
      AdvancedKeyframeAnimations.animate(this, p_288996_, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
   }
}
