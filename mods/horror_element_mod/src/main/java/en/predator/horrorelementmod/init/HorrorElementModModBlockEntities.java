package en.predator.horrorelementmod.init;

import en.predator.horrorelementmod.block.entity.AtmosphereBlockBlockEntity;
import en.predator.horrorelementmod.block.entity.SoundBlockBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD
)
public class HorrorElementModModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "horror_element_mod");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> SOUND_BLOCK = register(
      "sound_block", HorrorElementModModBlocks.SOUND_BLOCK, SoundBlockBlockEntity::new
   );
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ATMOSPHERE_BLOCK = register(
      "atmosphere_block", HorrorElementModModBlocks.ATMOSPHERE_BLOCK, AtmosphereBlockBlockEntity::new
   );

   private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(
      String registryname, DeferredHolder<Block, Block> block, BlockEntitySupplier<?> supplier
   ) {
      return REGISTRY.register(registryname, () -> Builder.of(supplier, new Block[]{(Block)block.get()}).build(null));
   }

   @SubscribeEvent
   public static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(
         ItemHandler.BLOCK, (BlockEntityType)SOUND_BLOCK.get(), (blockEntity, side) -> ((SoundBlockBlockEntity)blockEntity).getItemHandler()
      );
      event.registerBlockEntity(
         ItemHandler.BLOCK, (BlockEntityType)ATMOSPHERE_BLOCK.get(), (blockEntity, side) -> ((AtmosphereBlockBlockEntity)blockEntity).getItemHandler()
      );
   }
}
