package com.gametechbc.traveloptics.effects;

import com.gametechbc.traveloptics.util.TOParticleHelper;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class AstralPathRevealEffect extends MobEffect {
   public AstralPathRevealEffect() {
      super(MobEffectCategory.NEUTRAL, 11393254);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide && entity instanceof Mob mob) {
         Path path = mob.getNavigation().getPath();
         if (path != null && !path.isDone()) {
            ServerLevel level = (ServerLevel)entity.level();
            int lastNodeIndex = path.getNodeCount() - 1;

            for (int i = path.getNextNodeIndex(); i < lastNodeIndex; i++) {
               Node node = path.getNode(i);
               BlockPos pos = new BlockPos(node.x, node.y, node.z);
               Vec3 particlePos = Vec3.atBottomCenterOf(pos).add(0.0, 0.1, 0.0);
               MagicManager.spawnParticles(
                  level, TOParticleHelper.ABYSS_SPIKE_PARTICLE, particlePos.x, particlePos.y + 0.165F, particlePos.z, 1, 0.0, 0.0, 0.0, 0.01, true
               );
            }

            if (lastNodeIndex >= 0) {
               Node lastNode = path.getNode(lastNodeIndex);
               Vec3 endPos = new Vec3(lastNode.x + 0.5, lastNode.y + 0.165F, lastNode.z + 0.5);
               MagicManager.spawnParticles(
                  level,
                  new BlastwaveParticleOptions(((SchoolType)SchoolRegistry.ENDER.get()).getTargetingColor(), 1.5F),
                  endPos.x,
                  endPos.y,
                  endPos.z,
                  1,
                  0.0,
                  0.0,
                  0.0,
                  0.0,
                  true
               );
            }
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 20 == 0;
   }
}
