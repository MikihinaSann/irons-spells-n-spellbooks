package it.crystalnest.prometheus.api.block;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class CustomTorchBlock extends TorchBlock implements FireTyped {
   private final ResourceLocation fireType;
   private final Supplier<SimpleParticleType> type;

   public CustomTorchBlock(ResourceLocation fireType, Supplier<SimpleParticleType> type, Properties properties) {
      this(fireType, type, true, properties);
   }

   public CustomTorchBlock(ResourceLocation fireType, Supplier<SimpleParticleType> type, boolean addDefaultProperties, Properties properties) {
      super(null, (addDefaultProperties ? addDefaultProperties(properties) : properties).lightLevel(FireManager.lightLevel(fireType)));
      this.fireType = fireType;
      this.type = type;
   }

   private static Properties addDefaultProperties(Properties properties) {
      return properties.noCollission().instabreak().sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY);
   }

   public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
      this.flameParticle = this.type.get();
      super.animateTick(state, level, pos, random);
   }

   @Override
   public ResourceLocation getFireType() {
      return this.fireType;
   }
}
