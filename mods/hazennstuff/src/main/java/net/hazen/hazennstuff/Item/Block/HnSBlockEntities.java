package net.hazen.hazennstuff.Item.Block;

import java.util.function.Supplier;
import net.hazen.hazennstuff.Item.Block.Statues.HazelStatue.HazelStatueBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HnSBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "hazennstuff");
   public static final Supplier<BlockEntityType<HazelStatueBlockEntity>> HAZEL_STATUE = BLOCK_ENTITIES.register(
      "hazel_statue", () -> Builder.of(HazelStatueBlockEntity::new, new Block[]{(Block)HnSBlocks.HAZEL_STATUE.get()}).build(null)
   );

   public static void register(IEventBus eventBus) {
      BLOCK_ENTITIES.register(eventBus);
   }
}
