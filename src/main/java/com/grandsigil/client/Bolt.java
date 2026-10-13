package com.grandsigil.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public final class Bolt {
   private static final int MAX_ARCS = 900;
   private static final float BRANCH_CHANCE = 0.32F;
   private static final float BRANCH_REACH = 0.75F;
   private static final float BRANCH_WIDTH = 0.55F;
   private static final float BRANCH_ALPHA = 0.62F;
   private static final float CHAOS_DECAY = 0.58F;

   private Bolt() {
   }

   public static void generate(
      List<Bolt.Arc> out, Vector3f from, Vector3f to, RandomSource random, int levels, float chaos, float width, float alpha, int branches
   ) {
      split(out, from, to, random, levels, chaos, width, alpha, branches);
   }

   private static void split(List<Bolt.Arc> out, Vector3f a, Vector3f b, RandomSource random, int levels, float chaos, float width, float alpha, int branches) {
      if (out.size() < 900) {
         if (levels <= 0) {
            out.add(new Bolt.Arc(new Vector3f(a), new Vector3f(b), width, alpha));
         } else {
            Vector3f run = new Vector3f(b).sub(a);
            float length = run.length();
            if (!(length < 1.0E-4F)) {
               Vector3f mid = new Vector3f(a).add(b).mul(0.5F);
               Vector3f sideways = perpendicular(run, random);
               mid.add(sideways.mul(length * chaos * (random.nextFloat() - 0.5F) * 2.0F));
               float next = chaos * 0.58F;
               split(out, a, mid, random, levels - 1, next, width, alpha, branches);
               split(out, mid, b, random, levels - 1, next, width, alpha, branches);
               if (branches > 0 && random.nextFloat() < 0.32F) {
                  Vector3f heading = new Vector3f(b).sub(mid).normalize();
                  heading.add(perpendicular(heading, random).mul(0.55F + random.nextFloat() * 0.8F));
                  heading.normalize().mul(length * 0.75F * (0.45F + random.nextFloat() * 0.7F));
                  Vector3f tip = new Vector3f(mid).add(heading);
                  split(out, mid, tip, random, levels - 1, chaos * 1.15F, width * 0.55F, alpha * 0.62F, branches - 1);
               }
            }
         }
      }
   }

   private static Vector3f perpendicular(Vector3f run, RandomSource random) {
      Vector3f axis = Math.abs(run.y) > 0.9F * run.length() ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      Vector3f u = new Vector3f(run).cross(axis).normalize();
      Vector3f v = new Vector3f(run).cross(u).normalize();
      float angle = random.nextFloat() * (float) (Math.PI * 2);
      return u.mul(Mth.cos(angle)).add(v.mul(Mth.sin(angle))).normalize();
   }

   public static void onSphere(
      List<Bolt.Arc> out,
      Vector3f centre,
      float radius,
      Vector3f from,
      Vector3f to,
      RandomSource random,
      int levels,
      float chaos,
      float width,
      float alpha,
      int branches
   ) {
      List<Bolt.Arc> raw = new ArrayList<>();
      split(raw, from, to, random, levels, chaos, width, alpha, branches);

      for (Bolt.Arc arc : raw) {
         out.add(new Bolt.Arc(project(arc.from(), centre, radius), project(arc.to(), centre, radius), arc.width(), arc.alpha()));
      }
   }

   private static Vector3f project(Vector3f point, Vector3f centre, float radius) {
      Vector3f out = new Vector3f(point).sub(centre);
      float length = out.length();
      return length < 1.0E-4F ? new Vector3f(centre) : out.mul(radius / length).add(centre);
   }

   public static record Arc(Vector3f from, Vector3f to, float width, float alpha) {
   }
}
