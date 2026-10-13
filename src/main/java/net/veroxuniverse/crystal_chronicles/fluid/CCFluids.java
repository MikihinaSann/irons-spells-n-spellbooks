package net.veroxuniverse.crystal_chronicles.fluid;

import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Flowing;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.veroxuniverse.crystal_chronicles.block.BloodFluidBlock;
import net.veroxuniverse.crystal_chronicles.block.ReactiveWaterBlock;
import net.veroxuniverse.crystal_chronicles.registry.CCBlocks;
import net.veroxuniverse.crystal_chronicles.registry.CCItems;

public class CCFluids {
   public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, "crystal_chronicles");
   public static final Supplier<FlowingFluid> SOURCE_BLOOD = FLUIDS.register("source_blood", () -> new Source(CCFluids.BLOOD_PROPERTIES));
   public static final Supplier<FlowingFluid> FLOWING_BLOOD = FLUIDS.register("flowing_blood", () -> new Flowing(CCFluids.BLOOD_PROPERTIES));
   public static final Supplier<FlowingFluid> SOURCE_REACTIVE_WATER = FLUIDS.register("reactive_water", () -> new Source(CCFluids.REACTIVE_WATER_PROPERTIES));
   public static final Supplier<FlowingFluid> FLOWING_REACTIVE_WATER = FLUIDS.register(
      "reactive_water_flowing", () -> new Flowing(CCFluids.REACTIVE_WATER_PROPERTIES)
   );
   public static final DeferredBlock<LiquidBlock> BLOOD_BLOCK = CCBlocks.BLOCKS
      .register("blood_block", () -> new BloodFluidBlock(SOURCE_BLOOD.get(), Properties.ofFullCopy(Blocks.WATER).noLootTable()));
   public static final DeferredItem<Item> BLOOD_BUCKET = CCItems.ITEMS
      .registerItem("blood_bucket", properties -> new BucketItem(SOURCE_BLOOD.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
   public static final DeferredBlock<LiquidBlock> REACTIVE_WATER_BLOCK = CCBlocks.BLOCKS
      .register("reactive_water_block", () -> new ReactiveWaterBlock(SOURCE_REACTIVE_WATER.get(), Properties.ofFullCopy(Blocks.WATER).noLootTable()));
   public static final DeferredItem<Item> REACTIVE_WATER_BUCKET = CCItems.ITEMS
      .registerItem("reactive_water_bucket", properties -> new BucketItem(SOURCE_REACTIVE_WATER.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1)));
   public static final net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties BLOOD_PROPERTIES = new net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties(
         CCFluidTypes.BLOOD_FLUID_TYPE, SOURCE_BLOOD, FLOWING_BLOOD
      )
      .slopeFindDistance(2)
      .levelDecreasePerBlock(1)
      .block(BLOOD_BLOCK)
      .bucket(BLOOD_BUCKET)
      .explosionResistance(10.0F);
   public static final net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties REACTIVE_WATER_PROPERTIES = new net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties(
         CCFluidTypes.REACTIVE_WATER_FLUID_TYPE, SOURCE_REACTIVE_WATER, FLOWING_REACTIVE_WATER
      )
      .slopeFindDistance(4)
      .levelDecreasePerBlock(1)
      .block(REACTIVE_WATER_BLOCK)
      .bucket(REACTIVE_WATER_BUCKET)
      .explosionResistance(100.0F);

   public static void register(IEventBus eventBus) {
      FLUIDS.register(eventBus);
   }
}
