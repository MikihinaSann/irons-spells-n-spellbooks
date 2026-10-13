package io.redspace.ironslib.statue.worldgen;

import com.mojang.serialization.MapCodec;
import io.redspace.ironslib.patreon.PatreonData;
import io.redspace.ironslib.patreon.data.ProfileResult;
import io.redspace.ironslib.registry.IronsLibRegistries;
import io.redspace.ironslib.statue.PlayerStatuePose;
import io.redspace.ironslib.statue.block.statue_block.AbstractStatueBlock;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.jetbrains.annotations.NotNull;

public class RandomizePlayerStatueProcessor extends StructureProcessor {
   public static final MapCodec<RandomizePlayerStatueProcessor> MAP_CODEC = MapCodec.unit(RandomizePlayerStatueProcessor::new);

   @Nullable
   public StructureBlockInfo process(
      @NotNull LevelReader level,
      @NotNull BlockPos blockPos,
      @NotNull BlockPos pivot,
      StructureBlockInfo blockInfo,
      @NotNull StructureBlockInfo relativeBlockInfo,
      @NotNull StructurePlaceSettings settings,
      @Nullable StructureTemplate template
   ) {
      BlockState state = blockInfo.state();
      if (!state.is((Block)IronsLibRegistries.BlockRegistry.PLAYER_STATUE_BLOCK.get())
         && !state.is((Block)IronsLibRegistries.Legacy.BlockRegistry.PLAYER_STATUE_BLOCK.get())) {
         return relativeBlockInfo;
      } else {
         boolean isPrimary = (Integer)state.getValue(AbstractStatueBlock.X_POS) == 0
            && (Integer)state.getValue(AbstractStatueBlock.Y_POS) == 0
            && (Integer)state.getValue(AbstractStatueBlock.Z_POS) == 0;
         if (!isPrimary) {
            return relativeBlockInfo;
         } else {
            List<ProfileResult> profiles = PatreonData.getInstance()
               .getProfileValues()
               .stream()
               .filter(profileResult -> profileResult.permissions().supportsStatues())
               .toList();
            if (profiles.isEmpty()) {
               return relativeBlockInfo;
            } else {
               ProfileResult profile = profiles.get((int)(Math.random() * (double)profiles.size()));
               UUID uuid = profile.uuid();
               PlayerStatuePose pose = PlayerStatuePose.values()[(int)(Math.random() * (double)PlayerStatuePose.values().length)];
               CompoundTag tag = relativeBlockInfo.nbt();
               if (tag == null) {
                  tag = new CompoundTag();
               }

               tag.putUUID("playerUuid", uuid);
               tag.putString("pose", pose.getSerializedName());
               return new StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), tag);
            }
         }
      }
   }

   @NotNull
   protected StructureProcessorType<?> getType() {
      return (StructureProcessorType<?>)IronsLibRegistries.StructureProcessorRegistry.RANDOMIZE_PLAYER_STATUE_PROCESSOR_TYPE.get();
   }
}
