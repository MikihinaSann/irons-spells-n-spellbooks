package com.somake.somakespells.mixin;

import com.github.L_Ender.cataclysm.blockentities.AltarOfFire_Block_Entity;
import com.github.L_Ender.cataclysm.blocks.Altar_Of_Fire_Block;
import com.somake.somakespells.block.entity.SoulFireAltarState;
import com.somake.somakespells.registries.ModParticles;
import com.somake.somakespells.ritual.RitualElementalMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Altar_Of_Fire_Block.class})
public abstract class AltarOfFireBlockMixin {
   @Redirect(
      method = {"animateTick"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
      )
   )
   private void somake$swapAmbientLavaParticle(
      Level level,
      ParticleOptions particleOptions,
      double x,
      double y,
      double z,
      double velocityX,
      double velocityY,
      double velocityZ,
      BlockState state,
      Level blockLevel,
      BlockPos pos,
      RandomSource random
   ) {
      level.addParticle(resolveAmbientParticle(level, pos, particleOptions), x, y, z, velocityX, velocityY, velocityZ);
   }

   private static ParticleOptions resolveAmbientParticle(Level level, BlockPos pos, ParticleOptions original) {
      if (level.getBlockEntity(pos) instanceof AltarOfFire_Block_Entity altar && altar instanceof SoulFireAltarState ritualState) {
         RitualElementalMode var6 = ritualState.somake$getRitualMode();

         return switch (var6) {
            case SOUL_FIRE -> (SimpleParticleType)ModParticles.SOUL_FIRE_LAVA.get();
            case INFERNAL_FIRE -> ModParticles.infernalFireLavaOr((SimpleParticleType)ModParticles.SOUL_FIRE_LAVA.get());
            default -> ParticleTypes.LAVA;
         };
      } else {
         return original;
      }
   }
}
