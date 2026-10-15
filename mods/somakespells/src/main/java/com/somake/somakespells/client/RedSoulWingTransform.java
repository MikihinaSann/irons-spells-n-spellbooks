package com.somake.somakespells.client;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public final class RedSoulWingTransform {
   private static final float DEFAULT_X = 0.02F;
   private static final float DEFAULT_Y = 0.2F;
   private static final float DEFAULT_Z = 0.35F;
   private static final float DEFAULT_ROT_X = 0.0F;
   private static final float DEFAULT_ROT_Y = 180.0F;
   private static final float DEFAULT_ROT_Z = 5.4F;
   private static final float DEFAULT_SCALE = 0.8F;
   private static float x = 0.02F;
   private static float y = 0.2F;
   private static float z = 0.35F;
   private static float rotX = 0.0F;
   private static float rotY = 180.0F;
   private static float rotZ = 5.4F;
   private static float scale = 0.8F;
   private static RedSoulWingTransform.WingState previewState = RedSoulWingTransform.WingState.AUTO;
   private static final Map<RedSoulWingTransform.WingState, RedSoulWingTransform.WingPose> DEFAULT_POSES = createDefaultPoses();
   private static final Map<RedSoulWingTransform.WingState, RedSoulWingTransform.WingPose> poses = new EnumMap<>(DEFAULT_POSES);

   private RedSoulWingTransform() {
   }

   public static void setPosition(float x, float y, float z) {
      RedSoulWingTransform.x = x;
      RedSoulWingTransform.y = y;
      RedSoulWingTransform.z = z;
   }

   public static void setRotation(float x, float y, float z) {
      rotX = x;
      rotY = y;
      rotZ = z;
   }

   public static void setScale(float value) {
      scale = value;
   }

   public static void setPoseDegrees(RedSoulWingTransform.WingState state, float x, float y, float z, float edgeY) {
      if (state != RedSoulWingTransform.WingState.AUTO) {
         poses.put(state, RedSoulWingTransform.WingPose.fromDegrees(x, y, z, edgeY));
      }
   }

   public static void setPreviewState(RedSoulWingTransform.WingState state) {
      previewState = state;
   }

   public static void resetPose(RedSoulWingTransform.WingState state) {
      if (state != RedSoulWingTransform.WingState.AUTO) {
         poses.put(state, DEFAULT_POSES.get(state));
      }
   }

   public static void reset() {
      setPosition(0.02F, 0.2F, 0.35F);
      setRotation(0.0F, 180.0F, 5.4F);
      setScale(0.8F);
      poses.clear();
      poses.putAll(DEFAULT_POSES);
      previewState = RedSoulWingTransform.WingState.AUTO;
   }

   public static String describe() {
      return String.format(
         Locale.ROOT, "pos=(%.4f, %.4f, %.4f) rot=(%.2f, %.2f, %.2f) scale=%.4f preview=%s", x, y, z, rotX, rotY, rotZ, scale, previewState.id()
      );
   }

   public static String describePose(RedSoulWingTransform.WingState state) {
      RedSoulWingTransform.WingPose pose = poses.get(state);
      return pose == null
         ? state.id() + "=auto"
         : String.format(
            Locale.ROOT, "%s rot=(%.2f, %.2f, %.2f) edge_y=%.2f", state.id(), pose.xDegrees(), pose.yDegrees(), pose.zDegrees(), pose.edgeYDegrees()
         );
   }

   public static float x() {
      return x;
   }

   public static float y() {
      return y;
   }

   public static float z() {
      return z;
   }

   public static float rotX() {
      return rotX;
   }

   public static float rotY() {
      return rotY;
   }

   public static float rotZ() {
      return rotZ;
   }

   public static float scale() {
      return scale;
   }

   public static RedSoulWingTransform.WingState previewState() {
      return previewState;
   }

   public static RedSoulWingTransform.WingPose pose(RedSoulWingTransform.WingState state) {
      RedSoulWingTransform.WingState resolved = state == RedSoulWingTransform.WingState.AUTO ? RedSoulWingTransform.WingState.IDLE : state;
      return poses.getOrDefault(resolved, DEFAULT_POSES.get(RedSoulWingTransform.WingState.IDLE));
   }

   private static Map<RedSoulWingTransform.WingState, RedSoulWingTransform.WingPose> createDefaultPoses() {
      Map<RedSoulWingTransform.WingState, RedSoulWingTransform.WingPose> defaults = new EnumMap<>(RedSoulWingTransform.WingState.class);
      defaults.put(RedSoulWingTransform.WingState.IDLE, RedSoulWingTransform.WingPose.fromRadians(-0.08F, -1.55F, -0.18F, -0.95F));
      defaults.put(RedSoulWingTransform.WingState.WALK, RedSoulWingTransform.WingPose.fromRadians(-0.08F, -1.8F, -0.2925F, -1.125F));
      defaults.put(RedSoulWingTransform.WingState.SPRINT, RedSoulWingTransform.WingPose.fromRadians(-0.08F, -1.97F, -0.369F, -1.244F));
      defaults.put(RedSoulWingTransform.WingState.AIR, RedSoulWingTransform.WingPose.fromDegrees(-4.2F, -120.32F, -14.49F, -60.0F));
      defaults.put(RedSoulWingTransform.WingState.CROUCH, RedSoulWingTransform.WingPose.fromDegrees(-4.58F, -90.0F, -0.52F, -39.19F));
      defaults.put(RedSoulWingTransform.WingState.CAST, RedSoulWingTransform.WingPose.fromRadians(-0.08F, -2.03F, -0.396F, -1.286F));
      return defaults;
   }

   public record WingPose(float x, float y, float z, float edgeY) {
      private static RedSoulWingTransform.WingPose fromRadians(float x, float y, float z, float edgeY) {
         return new RedSoulWingTransform.WingPose(x, y, z, edgeY);
      }

      private static RedSoulWingTransform.WingPose fromDegrees(float x, float y, float z, float edgeY) {
         return new RedSoulWingTransform.WingPose((float)Math.toRadians(x), (float)Math.toRadians(y), (float)Math.toRadians(z), (float)Math.toRadians(edgeY));
      }

      public float xDegrees() {
         return (float)Math.toDegrees(this.x);
      }

      public float yDegrees() {
         return (float)Math.toDegrees(this.y);
      }

      public float zDegrees() {
         return (float)Math.toDegrees(this.z);
      }

      public float edgeYDegrees() {
         return (float)Math.toDegrees(this.edgeY);
      }
   }

   public static enum WingState {
      AUTO("auto"),
      IDLE("idle"),
      WALK("walk"),
      SPRINT("sprint"),
      AIR("air"),
      CROUCH("crouch"),
      CAST("cast");

      private final String id;

      private WingState(String id) {
         this.id = id;
      }

      public String id() {
         return this.id;
      }
   }
}
