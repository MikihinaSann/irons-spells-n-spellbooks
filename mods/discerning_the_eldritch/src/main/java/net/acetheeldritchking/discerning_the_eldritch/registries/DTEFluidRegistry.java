package net.acetheeldritchking.discerning_the_eldritch.registries;

import io.redspace.ironsspellbooks.fluids.NoopFluid;
import java.util.function.Supplier;
import net.acetheeldritchking.discerning_the_eldritch.DiscerningTheEldritch;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidType.Properties;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DTEFluidRegistry {
   private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, "discerning_the_eldritch");
   private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, "discerning_the_eldritch");
   public static final DeferredHolder<FluidType, FluidType> LIQUID_MALICE_TYPE = FLUID_TYPES.register("liquid_malice", () -> new FluidType(Properties.create()));
   public static final DeferredHolder<Fluid, NoopFluid> LIQUID_MALICE = registerNoop("liquid_malice", LIQUID_MALICE_TYPE::value);

   private static DeferredHolder<Fluid, NoopFluid> registerNoop(String name, Supplier<FluidType> fluidType) {
      DeferredHolder<Fluid, NoopFluid> holder = DeferredHolder.create(Registries.FLUID, DiscerningTheEldritch.id(name));
      net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties properties = new net.neoforged.neoforge.fluids.BaseFlowingFluid.Properties(
            fluidType, holder::value, holder::value
         )
         .bucket(() -> Items.AIR);
      FLUIDS.register(name, () -> new NoopFluid(properties));
      return holder;
   }

   public static void register(IEventBus eventBus) {
      FLUIDS.register(eventBus);
      FLUID_TYPES.register(eventBus);
   }
}
