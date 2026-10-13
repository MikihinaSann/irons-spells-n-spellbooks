package com.grandsigil.client;

import com.grandsigil.entity.PhoenixEntity;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public final class PhoenixPose {
   private static final float STROKE = 18.0F;
   private static final float BANK = 0.2F;
   private static final float SIZE = 3.0F;
   public final Vector3f at = new Vector3f();
   public final Vector3f fwd = new Vector3f();
   public final Vector3f up = new Vector3f();
   public final Vector3f right = new Vector3f();
   public float scale = 1.0F;
   public float alpha = 1.0F;
   public float spread = 1.0F;
   public float stroke;
   public final Vector3f[] shoulder = new Vector3f[]{new Vector3f(), new Vector3f()};
   public final Vector3f[] wrist = new Vector3f[]{new Vector3f(), new Vector3f()};
   public final Vector3f[] tip = new Vector3f[]{new Vector3f(), new Vector3f()};
   public final Vector3f[] arm = new Vector3f[]{new Vector3f(), new Vector3f()};
   public final Vector3f[] hand = new Vector3f[]{new Vector3f(), new Vector3f()};
   public final Vector3f[] normal = new Vector3f[]{new Vector3f(), new Vector3f()};

   private PhoenixPose() {
   }

   public static PhoenixPose flight(PhoenixEntity phoenix, float t) {
      Vector3f s = phoenix.start();
      float e = phoenix.entry();
      float turn = phoenix.turn();
      PhoenixPose pose = new PhoenixPose();
      frame(pose, s, e, turn, t);
      if (t < 24.0F) {
         float u = t / 24.0F;
         pose.scale = 0.35F + 0.65F * PhoenixEntity.smooth(Mth.clamp(u * 1.3F, 0.0F, 1.0F));
         pose.alpha = Mth.clamp(u * 2.2F, 0.0F, 1.0F);
         pose.spread = PhoenixEntity.smooth(Mth.clamp((u - 0.35F) / 0.65F, 0.0F, 1.0F));
      } else if (t >= 222.0F) {
         float u = (t - 222.0F) / 14.0F;
         pose.spread = 1.0F - 0.85F * PhoenixEntity.smooth(Mth.clamp(u / 0.35F, 0.0F, 1.0F));
      }

      pose.scale *= 3.0F;
      wings(pose, amplitude(t), t);
      return pose;
   }

   public static PhoenixPose ghost(float since, Vector3f back) {
      PhoenixPose pose = new PhoenixPose();
      float u = Mth.clamp(since / 44.0F, 0.0F, 1.0F);
      float rise = 4.0F + 34.0F * (1.0F - (1.0F - u) * (1.0F - u));
      pose.at.set(0.0F, rise, 0.0F);
      pose.fwd.set(0.0F, 1.0F, 0.0F);
      pose.up.set(back).normalize();
      pose.right.set(pose.fwd).cross(pose.up).normalize();
      pose.scale = 2.8F + 0.7F * u;
      pose.spread = PhoenixEntity.smooth(Mth.clamp(since / 12.0F, 0.0F, 1.0F));
      pose.alpha = Mth.clamp(since / 6.0F, 0.0F, 1.0F) * (1.0F - PhoenixEntity.smooth(Mth.clamp((since - 18.0F) / 24.0F, 0.0F, 1.0F)));
      wings(pose, 0.45F * (1.0F - u), since * 0.7F + 4.0F);
      return pose;
   }

   private static void frame(PhoenixPose pose, Vector3f s, float e, float turn, float t) {
      Vector3f here = PhoenixEntity.at(t, s, e, turn);
      Vector3f ahead = PhoenixEntity.at(t + 0.5F, s, e, turn);
      Vector3f behind = PhoenixEntity.at(t - 0.5F, s, e, turn);
      pose.at.set(here);
      Vector3f fwd = new Vector3f(ahead).sub(behind);
      if (fwd.lengthSquared() < 1.0E-6F) {
         fwd.set(0.0F, 1.0F, 0.0F);
      }

      fwd.normalize();
      pose.fwd.set(fwd);
      Vector3f accel = PhoenixEntity.at(t + 1.0F, s, e, turn).add(PhoenixEntity.at(t - 1.0F, s, e, turn)).sub(new Vector3f(here).mul(2.0F));
      Vector3f lateral = new Vector3f(accel).sub(new Vector3f(fwd).mul(fwd.dot(accel)));
      float vertical = fwd.y * fwd.y;
      Vector3f outward = new Vector3f(Mth.cos(e), 0.0F, Mth.sin(e));
      Vector3f ref = new Vector3f(0.0F, 0.2F, 0.0F).add(lateral).add(outward.mul(0.2F * vertical * 1.5F));
      Vector3f up = ref.sub(new Vector3f(fwd).mul(fwd.dot(ref)));
      if (up.lengthSquared() < 1.0E-8F) {
         up.set(1.0F, 0.0F, 0.0F).sub(new Vector3f(fwd).mul(fwd.x));
      }

      pose.up.set(up.normalize());
      pose.right.set(fwd).cross(pose.up).normalize();
   }

   private static float amplitude(float t) {
      float glide = 0.2F + 0.38F * (float)Math.pow((double)Math.max(0.0F, Mth.sin(t * 0.075F)), 2.0);
      float a = 0.25F;
      a = blend(t, 24.0F, a, 0.75F);
      a = blend(t, 54.0F, a, glide);
      a = blend(t, 198.0F, a, 0.9F);
      return blend(t, 222.0F, a, 0.0F);
   }

   private static float blend(float t, float at, float before, float after) {
      float k = PhoenixEntity.smooth(Mth.clamp((t - at + 4.0F) / 8.0F, 0.0F, 1.0F));
      return Mth.lerp(k, before, after);
   }

   private static void wings(PhoenixPose pose, float amp, float t) {
      float sc = pose.scale;
      float spread = pose.spread;
      float phase = t * (float) (Math.PI * 2) / 18.0F;
      pose.stroke = Mth.sin(phase);

      for (int w = 0; w < 2; w++) {
         float side = w == 0 ? 1.0F : -1.0F;
         Vector3f s = new Vector3f(pose.at).add(new Vector3f(pose.right).mul(side * 0.5F * sc)).add(new Vector3f(pose.up).mul(0.2F * sc));
         float lift = spread * (0.1F + amp * Mth.sin(phase)) - (1.0F - spread) * 0.55F;
         float sweep = 0.15F + (1.0F - spread) * 1.15F;
         Vector3f arm = direction(pose, side, lift, sweep);
         Vector3f wrist = new Vector3f(s).add(new Vector3f(arm).mul(3.0F * sc * Mth.lerp(spread, 0.6F, 1.0F)));
         float lift2 = lift + spread * amp * 0.55F * Mth.sin(phase - 1.0F) - (1.0F - spread) * 0.2F;
         float sweep2 = sweep + 0.3F + (1.0F - spread) * 0.5F;
         Vector3f hand = direction(pose, side, lift2, sweep2);
         Vector3f tip = new Vector3f(wrist).add(new Vector3f(hand).mul(3.3F * sc * Mth.lerp(spread, 0.55F, 1.0F)));
         pose.shoulder[w].set(s);
         pose.wrist[w].set(wrist);
         pose.tip[w].set(tip);
         pose.arm[w].set(arm);
         pose.hand[w].set(hand);
         Vector3f n = new Vector3f(arm).cross(pose.fwd);
         if (n.lengthSquared() < 1.0E-6F) {
            n.set(pose.up);
         }

         pose.normal[w].set(n.normalize());
      }
   }

   private static Vector3f direction(PhoenixPose pose, float side, float lift, float sweep) {
      Vector3f out = new Vector3f(pose.right).mul(side * Mth.cos(lift)).add(new Vector3f(pose.up).mul(Mth.sin(lift)));
      return out.mul(Mth.cos(sweep)).sub(new Vector3f(pose.fwd).mul(Mth.sin(sweep))).normalize();
   }

   public Vector3f along(int w, float k) {
      return k <= 0.5F ? new Vector3f(this.shoulder[w]).lerp(this.wrist[w], k * 2.0F) : new Vector3f(this.wrist[w]).lerp(this.tip[w], (k - 0.5F) * 2.0F);
   }
}
