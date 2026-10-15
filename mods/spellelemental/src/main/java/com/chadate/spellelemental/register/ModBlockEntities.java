package com.chadate.spellelemental.register;

import com.chadate.spellelemental.block.entity.SwordStandBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "spellelemental");
   public static final Supplier<BlockEntityType<SwordStandBlockEntity>> SWORD_STAND = BLOCK_ENTITIES.register(
      "sword_stand", () -> Builder.of(SwordStandBlockEntity::new, new Block[]{ModBlocks.SWORD_STAND.get()}).build(null)
   );

   public static void register(IEventBus eventBus) {
      BLOCK_ENTITIES.register(eventBus);
   }
}
