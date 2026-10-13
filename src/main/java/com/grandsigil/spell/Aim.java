package com.grandsigil.spell;

import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public final class Aim {
   private static final int SEARCH = 24;
   private static final double CEILING = 6.0;

   private Aim() {
   }

   public static Vec3 ground(ServerLevel level, LivingEntity caster, float range) {
      BlockHitResult hit = Utils.getTargetBlock(level, caster, Fluid.NONE, (double)range);
      Vec3 point = hit.getType() == Type.MISS ? flat(caster, range) : hit.getLocation();
      if (point.y > caster.getY() + 6.0) {
         point = new Vec3(point.x, caster.getY(), point.z);
      }

      return Utils.moveToRelativeGroundLevel(level, point, 24);
   }

   public static Vec3 beneath(ServerLevel level, Entity of) {
      return Utils.moveToRelativeGroundLevel(level, of.position(), 24);
   }

   @Nullable
   public static LivingEntity target(Level level, LivingEntity caster, float range) {
      HitResult hit = Utils.raycastForEntity(level, caster, range, true);
      if (hit.getType() == Type.ENTITY && ((EntityHitResult)hit).getEntity() instanceof LivingEntity found) {
         return found != caster && found.isAlive() && !found.isAlliedTo(caster) ? found : null;
      } else {
         return null;
      }
   }

   private static Vec3 flat(LivingEntity caster, float range) {
      Vec3 look = caster.getLookAngle();
      double horizontal = Math.sqrt(look.x * look.x + look.z * look.z);
      return horizontal < 1.0E-4 ? caster.position() : caster.position().add(look.x / horizontal * (double)range, 0.0, look.z / horizontal * (double)range);
   }
}
