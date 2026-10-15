package net.warphan.iss_magicfromtheeast.registries;

import io.redspace.ironsspellbooks.fluids.NoopFluid;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.warphan.iss_magicfromtheeast.ISS_MagicFromTheEast;

public class MFTEFluidRegistries {
   private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, "iss_magicfromtheeast");
   private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, "iss_magicfromtheeast");
   public static final DeferredHolder<FluidType, FluidType> SOUL_TYPE = FLUID_TYPES.register("soul", () -> new FluidType(Properties.create()));
   public static final DeferredHolder<Fluid, NoopFluid> SOUL = registerNoop("soul", SOUL_TYPE::value);

   public static void register(IEventBus eventBus) {
      FLUIDS.register(eventBus);
      FLUID_TYPES.register(eventBus);
   }

   private static DeferredHolder<Fluid, NoopFluid> registerNoop(String name, Supplier<FluidType> fluidType) {
      DeferredHolder<Fluid, NoopFluid> holder = DeferredHolder.create(Registries.FLUID, ISS_MagicFromTheEast.id(name));
      net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties properties = new net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties(
            fluidType, holder::value, holder::value
         )
         .bucket(() -> Items.AIR);
      FLUIDS.register(name, () -> new NoopFluid(properties));
      return holder;
   }
}
