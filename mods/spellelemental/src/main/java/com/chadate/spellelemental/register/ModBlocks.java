package com.chadate.spellelemental.register;

import com.chadate.spellelemental.block.SwordStandBlock;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "spellelemental");
   public static final Supplier<Block> SWORD_STAND = BLOCKS.register(
      "sword_stand", () -> new SwordStandBlock(Properties.of().mapColor(MapColor.COLOR_PINK).strength(2.0F).sound(SoundType.CHERRY_WOOD).noOcclusion())
   );
   public static final Supplier<Block> STELLAR_FRAGMENT_ORE = BLOCKS.register(
      "stellar_fragment_ore",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.STONE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(4.0F, 3.0F)
            .sound(SoundType.STONE)
      )
   );
   public static final Supplier<Block> DEEPSLATE_STELLAR_FRAGMENT_ORE = BLOCKS.register(
      "deepslate_stellar_fragment_ore",
      () -> new Block(
         Properties.of()
            .mapColor(MapColor.DEEPSLATE)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(5.5F, 4.0F)
            .sound(SoundType.DEEPSLATE)
      )
   );

   public static void register(IEventBus eventBus) {
      BLOCKS.register(eventBus);
   }
}
