package it.crystalnest.prometheus.api.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.crystalnest.prometheus.api.Fire;
import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.block.entity.CustomCampfireBlockEntity;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomCampfireBlock extends CampfireBlock implements FireTyped {
   public static final MapCodec<CampfireBlock> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("fire_type").forGetter(block -> ((FireTyped)block).getFireType()),
            Codec.BOOL.fieldOf("spawn_particles").forGetter(block -> block.spawnParticles),
            propertiesCodec()
         )
         .apply(instance, CustomCampfireBlock::new)
   );
   private final ResourceLocation fireType;

   public CustomCampfireBlock(ResourceLocation fireType, boolean spawnParticles, Properties properties) {
      this(fireType, spawnParticles, true, properties);
   }

   public CustomCampfireBlock(ResourceLocation fireType, boolean spawnParticles, boolean addDefaultProperties, Properties properties) {
      super(
         spawnParticles,
         Math.round(FireManager.getProperty(fireType, Fire::getDamage)),
         (addDefaultProperties ? addDefaultProperties(properties) : properties)
            .lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? FireManager.light(fireType) : 0)
      );
      this.fireType = fireType;
   }

   private static Properties addDefaultProperties(Properties properties) {
      return properties.mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).noOcclusion().ignitedByLava();
   }

   protected BlockEntityType<CustomCampfireBlockEntity> getBlockEntityType() {
      return (BlockEntityType<CustomCampfireBlockEntity>)FireManager.getCustomCampfireEntityType().get();
   }

   protected BlockEntityTicker<CampfireBlockEntity> particleTick() {
      return CampfireBlockEntity::particleTick;
   }

   protected BlockEntityTicker<CampfireBlockEntity> cookTick() {
      return CampfireBlockEntity::cookTick;
   }

   protected BlockEntityTicker<CampfireBlockEntity> cooldownTick() {
      return CampfireBlockEntity::cooldownTick;
   }

   @NotNull
   public MapCodec<CampfireBlock> codec() {
      return CODEC;
   }

   public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
      return new CustomCampfireBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
      BlockEntityType<CustomCampfireBlockEntity> customBlockEntityType = this.getBlockEntityType();
      if (level.isClientSide) {
         return state.getValue(LIT) ? createTickerHelper(blockEntityType, customBlockEntityType, this.particleTick()) : null;
      } else {
         return state.getValue(LIT)
            ? createTickerHelper(blockEntityType, customBlockEntityType, this.cookTick())
            : createTickerHelper(blockEntityType, customBlockEntityType, this.cooldownTick());
      }
   }

   @Override
   public ResourceLocation getFireType() {
      return this.fireType;
   }
}
