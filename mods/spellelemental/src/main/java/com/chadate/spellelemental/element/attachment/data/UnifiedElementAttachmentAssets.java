package com.chadate.spellelemental.element.attachment.data;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public final class UnifiedElementAttachmentAssets {
   private static final Map<String, String> ELEMENT_ID_TO_ICON = new ConcurrentHashMap<>();
   private static final Map<String, String> ELEMENT_ID_TO_PARTICLE = new ConcurrentHashMap<>();
   private static final Map<String, String> ELEMENT_ID_TO_SCHOOL = new ConcurrentHashMap<>();

   private UnifiedElementAttachmentAssets() {
   }

   public static void clear() {
      ELEMENT_ID_TO_ICON.clear();
      ELEMENT_ID_TO_PARTICLE.clear();
      ELEMENT_ID_TO_SCHOOL.clear();
   }

   public static void setIcon(String attachmentTypeOrKey, String iconResource) {
      if (attachmentTypeOrKey != null && !attachmentTypeOrKey.isEmpty() && iconResource != null && !iconResource.isEmpty()) {
         String key = attachmentTypeOrKey.toLowerCase();
         ELEMENT_ID_TO_ICON.put(key, iconResource);
         String base = toBaseKey(key);
         ELEMENT_ID_TO_ICON.put(base, iconResource);
      }
   }

   public static String getIcon(String elementKeyOrAttachmentType) {
      if (elementKeyOrAttachmentType == null) {
         return null;
      } else {
         String key = elementKeyOrAttachmentType.toLowerCase();
         String icon = ELEMENT_ID_TO_ICON.get(key);
         return icon != null ? icon : ELEMENT_ID_TO_ICON.get(toBaseKey(key));
      }
   }

   public static void setParticleEffect(String attachmentTypeOrKey, String particleKey) {
      if (attachmentTypeOrKey != null && !attachmentTypeOrKey.isEmpty() && particleKey != null && !particleKey.isEmpty()) {
         String key = attachmentTypeOrKey.toLowerCase();
         ELEMENT_ID_TO_PARTICLE.put(key, particleKey);
         String base = toBaseKey(key);
         ELEMENT_ID_TO_PARTICLE.put(base, particleKey);
      }
   }

   public static String getParticleEffect(String elementKeyOrAttachmentType) {
      if (elementKeyOrAttachmentType == null) {
         return null;
      } else {
         String key = elementKeyOrAttachmentType.toLowerCase();
         String effect = ELEMENT_ID_TO_PARTICLE.get(key);
         return effect != null ? effect : ELEMENT_ID_TO_PARTICLE.get(toBaseKey(key));
      }
   }

   public static void setSchool(String elementIdOrKey, String school) {
      if (elementIdOrKey != null && !elementIdOrKey.isEmpty() && school != null && !school.isEmpty()) {
         String key = elementIdOrKey.toLowerCase();
         ELEMENT_ID_TO_SCHOOL.put(key, school);
         ELEMENT_ID_TO_SCHOOL.put(toBaseKey(key), school);
      }
   }

   public static String getSchool(String elementKeyOrAttachmentType) {
      if (elementKeyOrAttachmentType == null) {
         return null;
      } else {
         String key = elementKeyOrAttachmentType.toLowerCase();
         String s = ELEMENT_ID_TO_SCHOOL.get(key);
         return s != null ? s : ELEMENT_ID_TO_SCHOOL.get(toBaseKey(key));
      }
   }

   public static String getElementIdBySchool(String schoolName) {
      if (schoolName != null && !schoolName.isEmpty()) {
         String target = schoolName.toLowerCase();

         for (Entry<String, String> e : ELEMENT_ID_TO_SCHOOL.entrySet()) {
            String s = e.getValue();
            if (s != null && s.equalsIgnoreCase(target)) {
               return toBaseKey(e.getKey());
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static Map<String, String> getAllIcons() {
      return Map.copyOf(ELEMENT_ID_TO_ICON);
   }

   public static Map<String, String> getAllParticleEffects() {
      return Map.copyOf(ELEMENT_ID_TO_PARTICLE);
   }

   public static Map<String, String> getAllSchools() {
      return Map.copyOf(ELEMENT_ID_TO_SCHOOL);
   }

   private static String toBaseKey(String key) {
      if (key == null) {
         return "";
      } else {
         int idx = key.indexOf(58);
         return idx >= 0 ? key.substring(idx + 1) : key;
      }
   }
}
