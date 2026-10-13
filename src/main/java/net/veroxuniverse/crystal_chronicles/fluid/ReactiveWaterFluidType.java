package net.veroxuniverse.crystal_chronicles.fluid;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;
import org.joml.Vector3f;

public class ReactiveWaterFluidType extends FluidType {
   public ReactiveWaterFluidType(Properties properties) {
      super(properties);
   }

   public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
      int ALPHA_TRANSLUCENT = 119;
      int DEFAULT_TINT = 2007356885;
      final Vector3f FOG_COLOR = new Vector3f(1.0F, 0.9F, 0.5F);
      int MAX_SEARCH_DEPTH = 3;
      consumer.accept(
         new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
               return ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
               return ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
               if (getter != null && pos != null) {
                  List<Integer> foundColors = new ArrayList<>();

                  for (int yOffset = 1; yOffset <= 3; yOffset++) {
                     BlockPos baseSearchPos = pos.below(yOffset);
                     List<BlockPos> positionsToCheck = new ArrayList<>();
                     positionsToCheck.add(baseSearchPos);
                     positionsToCheck.add(baseSearchPos.relative(Direction.NORTH));
                     positionsToCheck.add(baseSearchPos.relative(Direction.SOUTH));
                     positionsToCheck.add(baseSearchPos.relative(Direction.EAST));
                     positionsToCheck.add(baseSearchPos.relative(Direction.WEST));
                     boolean foundSolidBlock = false;

                     for (BlockPos searchPos : positionsToCheck) {
                        BlockState bs = getter.getBlockState(searchPos);
                        int color = -1;
                        if (bs.is((Block)CCBlocks.GREEN_SULPHUR_POOL.get())) {
                           color = 65280;
                        } else if (bs.is((Block)CCBlocks.YELLOW_SULPHUR_POOL.get())) {
                           color = 16776960;
                        } else if (bs.is((Block)CCBlocks.ORANGE_SULPHUR_POOL.get())) {
                           color = 16746496;
                        } else if (bs.is((Block)CCBlocks.RED_SULPHUR_POOL.get())) {
                           color = 16711680;
                        } else if (bs.is((Block)CCBlocks.VERMILLION_SULPHUR_POOL.get())) {
                           color = 14492160;
                        }

                        if (color != -1) {
                           foundColors.add(color);
                        }

                        if (searchPos.equals(baseSearchPos) && bs.isSolidRender(getter, searchPos)) {
                           foundSolidBlock = true;
                        }
                     }

                     if (foundSolidBlock || getter.getBlockState(baseSearchPos).isAir()) {
                        break;
                     }
                  }

                  if (foundColors.isEmpty()) {
                     return 2007356885;
                  } else {
                     long totalRed = 0L;
                     long totalGreen = 0L;
                     long totalBlue = 0L;

                     for (int colorx : foundColors) {
                        totalRed += (long)(colorx >> 16 & 0xFF);
                        totalGreen += (long)(colorx >> 8 & 0xFF);
                        totalBlue += (long)(colorx & 0xFF);
                     }

                     int averageRed = (int)(totalRed / (long)foundColors.size());
                     int averageGreen = (int)(totalGreen / (long)foundColors.size());
                     int averageBlue = (int)(totalBlue / (long)foundColors.size());
                     int blendedColor = averageRed << 16 | averageGreen << 8 | averageBlue;
                     return 1996488704 | blendedColor;
                  }
               } else {
                  return 2007356885;
               }
            }

            public Vector3f modifyFogColor(
               Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor
            ) {
               return FOG_COLOR;
            }

            public void modifyFogRender(
               Camera camera, FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape
            ) {
               if (mode == FogMode.FOG_SKY || mode == FogMode.FOG_TERRAIN) {
                  RenderSystem.setShaderFogStart(1.0F);
                  RenderSystem.setShaderFogEnd(3.0F);
                  RenderSystem.setShaderFogShape(FogShape.SPHERE);
               }
            }
         }
      );
   }
}
