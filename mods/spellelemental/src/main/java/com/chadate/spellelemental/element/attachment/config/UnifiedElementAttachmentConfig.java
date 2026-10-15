package com.chadate.spellelemental.element.attachment.config;

import com.google.gson.annotations.SerializedName;

public class UnifiedElementAttachmentConfig {
   @SerializedName("element_id")
   private String elementId;
   @SerializedName("school")
   private String school;
   @SerializedName("visual")
   private UnifiedElementAttachmentConfig.VisualConfig visual;

   public UnifiedElementAttachmentConfig() {
   }

   public UnifiedElementAttachmentConfig(String elementId, String school) {
      this.elementId = elementId;
      this.school = school;
   }

   public String getElementId() {
      return this.elementId;
   }

   public void setElementId(String elementId) {
      this.elementId = elementId;
   }

   public String getSchool() {
      return this.school;
   }

   public void setSchool(String school) {
      this.school = school;
   }

   public UnifiedElementAttachmentConfig.VisualConfig getVisual() {
      return this.visual;
   }

   public void setVisual(UnifiedElementAttachmentConfig.VisualConfig visual) {
      this.visual = visual;
   }

   public static class VisualConfig {
      @SerializedName("particle_effect")
      private String particleEffect;
      @SerializedName("icon")
      private String icon;

      public String getParticleEffect() {
         return this.particleEffect;
      }

      public void setParticleEffect(String particleEffect) {
         this.particleEffect = particleEffect;
      }

      public String getIcon() {
         return this.icon;
      }

      public void setIcon(String icon) {
         this.icon = icon;
      }
   }
}
