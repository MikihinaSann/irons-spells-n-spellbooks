package net.veroxuniverse.crystal_chronicles.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VentBlock extends Block {
   private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   public VentBlock(Properties properties) {
      super(properties);
   }

   @Override
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Override
   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Override
   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      int count = 3 + random.nextInt(2);

      for (int i = 0; i < count; i++) {
         double x = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
         double y = (double)pos.getY() + 1.0 + random.nextDouble() * 0.2;
         double z = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
         level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.01 + random.nextDouble() * 0.02, 0.0);
         if (random.nextFloat() < 0.25F) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0.0, 0.01 + random.nextDouble() * 0.02, 0.0);
         }
      }
   }

   @Override
   public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
      super.stepOn(level, pos, state, entity);
      if (!level.isClientSide && entity instanceof Player player && !player.isCreative() && !player.isSpectator()) {
         player.igniteForSeconds(2.0F);
      }
   }
}
