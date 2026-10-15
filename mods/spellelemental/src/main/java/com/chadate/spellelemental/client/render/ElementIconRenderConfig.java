package com.chadate.spellelemental.client.render;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ElementIconRenderConfig {
   private static final ElementIconRenderConfig INSTANCE = new ElementIconRenderConfig();
   private float quadScale = 0.15F;
   private double verticalOffset = 0.6;
   private double horizontalSpacing = 0.35;
   private float alpha = 0.9F;
   private float zDepth = 0.1F;
   private boolean faceCamera = true;
   private boolean flipBillboard = false;
   private int flashingThreshold = 50;
   private float minAlpha = 0.25F;
   private float maxAlpha = 0.9F;
   private float maxFlashHz = 8.0F;
   private float minFlashHz = 2.0F;

   private ElementIconRenderConfig() {
   }

   public static ElementIconRenderConfig get() {
      return INSTANCE;
   }

   public float getQuadScale() {
      return this.quadScale;
   }

   public void setQuadScale(float quadScale) {
      this.quadScale = quadScale;
   }

   public double getVerticalOffset() {
      return this.verticalOffset;
   }

   public void setVerticalOffset(double verticalOffset) {
      this.verticalOffset = verticalOffset;
   }

   public double getHorizontalSpacing() {
      return this.horizontalSpacing;
   }

   public void setHorizontalSpacing(double horizontalSpacing) {
      this.horizontalSpacing = horizontalSpacing;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public void setAlpha(float alpha) {
      this.alpha = alpha;
   }

   public float getZDepth() {
      return this.zDepth;
   }

   public void setZDepth(float zDepth) {
      this.zDepth = zDepth;
   }

   public boolean isFaceCamera() {
      return this.faceCamera;
   }

   public void setFaceCamera(boolean faceCamera) {
      this.faceCamera = faceCamera;
   }

   public boolean isFlipBillboard() {
      return this.flipBillboard;
   }

   public void setFlipBillboard(boolean flipBillboard) {
      this.flipBillboard = flipBillboard;
   }

   public int getFlashingThreshold() {
      return this.flashingThreshold;
   }

   public void setFlashingThreshold(int flashingThreshold) {
      this.flashingThreshold = flashingThreshold;
   }

   public float getMinAlpha() {
      return this.minAlpha;
   }

   public void setMinAlpha(float minAlpha) {
      this.minAlpha = minAlpha;
   }

   public float getMaxAlpha() {
      return this.maxAlpha;
   }

   public void setMaxAlpha(float maxAlpha) {
      this.maxAlpha = maxAlpha;
   }

   public float getMaxFlashHz() {
      return this.maxFlashHz;
   }

   public void setMaxFlashHz(float maxFlashHz) {
      this.maxFlashHz = maxFlashHz;
   }

   public float getMinFlashHz() {
      return this.minFlashHz;
   }

   public void setMinFlashHz(float minFlashHz) {
      this.minFlashHz = minFlashHz;
   }
}
