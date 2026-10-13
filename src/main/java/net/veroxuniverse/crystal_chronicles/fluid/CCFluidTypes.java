package net.veroxuniverse.crystal_chronicles.fluid;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;
import org.joml.Vector3f;

public class CCFluidTypes {
   public static final ResourceLocation BLOOD_STILL_RL = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "block/blood_still");
   public static final ResourceLocation BLOOD_FLOWING_RL = ResourceLocation.fromNamespaceAndPath("crystal_chronicles", "block/blood_flow");
   public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, "crystal_chronicles");
   public static final Supplier<FluidType> BLOOD_FLUID_TYPE = registerFluidType(
      "blood_fluid",
      new BaseFluidType(BLOOD_STILL_RL, BLOOD_FLOWING_RL, new Vector3f(0.2118F, 0.0196F, 0.0431F), Properties.create().density(1000).viscosity(2000))
   );
   public static final Supplier<FluidType> REACTIVE_WATER_FLUID_TYPE = FLUID_TYPES.register(
      "reactive_water", () -> new ReactiveWaterFluidType(Properties.create())
   );

   private static Supplier<FluidType> registerFluidType(String name, FluidType fluidType) {
      return FLUID_TYPES.register(name, () -> fluidType);
   }

   public static void register(IEventBus eventBus) {
      FLUID_TYPES.register(eventBus);
   }
}
