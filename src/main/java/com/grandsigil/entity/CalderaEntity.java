package com.grandsigil.entity;

import com.grandsigil.client.CalderaParticles;
import com.grandsigil.fx.CalderaFx;
import com.grandsigil.registry.GsEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

public class CalderaEntity extends FieldEntity {
   private static final float VEINS = 0.3F;
   private static final int ARMS = 7;
   private static final int BOON_TICKS = 40;
   private static final int SCORCH_SECONDS = 4;
   private static final float RIM = 0.8F;
   private static final int RIDGE = 7;
   private static final int CONES = 5;
   private static final int CONE_HEIGHT = 8;
   private static final float CONE_SPAN = 5.5F;
   private static final float CONE_IN = 0.3F;
   private static final float CONE_OUT = 0.7F;
   private static final float CLEARING = 5.0F;

   public CalderaEntity(EntityType<? extends CalderaEntity> type, Level level) {
      super(type, level);
   }

   public CalderaEntity(Level level, Vec3 at, LivingEntity caster, float power, float radius) {
      super(GsEntities.CALDERA.get(), level, at, caster, power, radius);
   }

   @Nullable
   @Override
   protected BlockState pave(ServerLevel level, BlockPos at, BlockState was, float spread) {
      return this.vein(at, spread) ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.BASALT.defaultBlockState();
   }

   private boolean vein(BlockPos at, float spread) {
      float dx = (float)at.getX() - (float)this.getX();
      float dz = (float)at.getZ() - (float)this.getZ();
      float angle = (float)Math.atan2((double)dz, (double)dx);
      float step = (float) (Math.PI * 2.0 / 7.0);
      float turn = spread * 1.1F;
      float off = Math.abs(((angle + turn) % step + step) % step - step * 0.5F);
      float width = step * (0.1F + 0.3F * (1.0F - spread * 0.5F));
      int hash = at.getX() * 92837111 ^ at.getZ() * 689287499;
      float ragged = (float)(hash >>> 9 & 0xFF) / 255.0F * 0.35F;
      return off < width * (0.8F + ragged);
   }

   @Nullable
   @Override
   protected BlockState clear(ServerLevel level, BlockPos at, BlockState was) {
      if (was.is(BlockTags.LOGS)) {
         BlockState basalt = Blocks.BASALT.defaultBlockState();
         return was.hasProperty(RotatedPillarBlock.AXIS) ? basalt.setValue(RotatedPillarBlock.AXIS, was.getValue(RotatedPillarBlock.AXIS)) : basalt;
      } else if (was.is(BlockTags.LEAVES)) {
         int hash = at.getX() * 73856093 ^ at.getY() * 19349663 ^ at.getZ() * 83492791;
         return (hash >>> 7 & 0xFF) < 28 ? Blocks.SHROOMLIGHT.defaultBlockState() : Blocks.NETHER_WART_BLOCK.defaultBlockState();
      } else {
         return !was.is(BlockTags.FLOWERS) && !was.is(BlockTags.SAPLINGS) && !was.is(Blocks.SNOW) && !was.is(Blocks.VINE) && !was.canBeReplaced()
            ? null
            : Blocks.AIR.defaultBlockState();
      }
   }

   @Override
   protected int relief(float spread, int dx, int dz) {
      float d = Mth.sqrt((float)(dx * dx) + (float)dz * (float)dz);
      if (d < 5.0F) {
         return 0;
      } else {
         int height = 0;
         if (spread > 0.8F) {
            float t = (spread - 0.8F) / 0.19999999F;
            float angle = (float)Math.atan2((double)dz, (double)dx);
            float ragged = 0.62F + 0.38F * Mth.sin(angle * 5.3F) * Mth.cos(angle * 2.1F + 1.4F);
            height = Math.round(7.0F * Mth.sin(t * (float) Math.PI) * ragged);
         }

         return Math.max(height, this.cone(dx, dz));
      }
   }

   private int cone(int dx, int dz) {
      int tallest = 0;
      long seed = (long)this.getId() * 8461L + 17L;

      for (int i = 0; i < 5; i++) {
         float a = (float)((double)(seed >> i * 5 & 31L) / 32.0 * Math.PI * 2.0 + (double)((float)i * 2.399963F));
         float out = this.radius() * (0.3F + 0.39999998F * ((float)(seed >> i * 3 + 2 & 7L) / 8.0F));
         float cx = Mth.cos(a) * out;
         float cz = Mth.sin(a) * out;
         float span = 5.5F * (0.7F + 0.6F * ((float)(seed >> i * 4 + 1 & 7L) / 8.0F));
         float d = Mth.sqrt(((float)dx - cx) * ((float)dx - cx) + ((float)dz - cz) * ((float)dz - cz));
         if (!(d >= span)) {
            float t = 1.0F - d / span;
            tallest = Math.max(tallest, Math.round(8.0F * Math.min(1.0F, t * 1.35F)));
         }
      }

      return tallest;
   }

   @Override
   protected BlockState raised(int layer, int height, float spread, BlockState ground) {
      return layer >= height - 1 && height > 2 ? Blocks.MAGMA_BLOCK.defaultBlockState() : Blocks.BASALT.defaultBlockState();
   }

   @Override
   protected boolean ours(BlockState state) {
      return state.is(Blocks.BASALT) || state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.NETHER_WART_BLOCK) || state.is(Blocks.SHROOMLIGHT) || state.isAir();
   }

   @Override
   protected void favour(ServerLevel level, LivingEntity ally) {
      ally.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false, true));
   }

   @Override
   protected void oppose(ServerLevel level, LivingEntity foe) {
      if (!foe.fireImmune() && !foe.hasEffect(MobEffects.FIRE_RESISTANCE)) {
         foe.igniteForSeconds(4.0F);
         foe.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, true, false, true));
      }
   }

   @Override
   protected void opened(ServerLevel level) {
      CalderaFx.opens(level, this.position(), (double)this.radius());
   }

   @Override
   protected void standing(ServerLevel level) {
      CalderaFx.rumbles(level, this.position(), (double)this.radius());
   }

   @Override
   protected void closed(ServerLevel level) {
      CalderaFx.closes(level, this.position(), (double)this.radius());
   }

   @Override
   protected void clientTick() {
      if (FMLEnvironment.dist.isClient()) {
         CalderaParticles.ambient(this);
      }
   }
}
