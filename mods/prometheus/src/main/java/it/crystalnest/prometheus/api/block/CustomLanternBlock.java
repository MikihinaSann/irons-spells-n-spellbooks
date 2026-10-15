package it.crystalnest.prometheus.api.block;

import it.crystalnest.prometheus.api.FireManager;
import it.crystalnest.prometheus.api.type.FireTyped;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CustomLanternBlock extends LanternBlock implements FireTyped {
   private final ResourceLocation fireType;

   public CustomLanternBlock(ResourceLocation fireType, Properties properties) {
      this(fireType, true, properties);
   }

   public CustomLanternBlock(ResourceLocation fireType, boolean addDefaultProperties, Properties properties) {
      super((addDefaultProperties ? addDefaultProperties(properties) : properties).lightLevel(FireManager.lightLevel(fireType)));
      this.fireType = fireType;
   }

   private static Properties addDefaultProperties(Properties properties) {
      return properties.mapColor(MapColor.METAL)
         .forceSolidOn()
         .requiresCorrectToolForDrops()
         .strength(3.5F)
         .sound(SoundType.LANTERN)
         .noOcclusion()
         .pushReaction(PushReaction.DESTROY);
   }

   @Override
   public ResourceLocation getFireType() {
      return this.fireType;
   }
}
