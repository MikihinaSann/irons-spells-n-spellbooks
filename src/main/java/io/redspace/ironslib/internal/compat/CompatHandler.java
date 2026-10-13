package io.redspace.ironslib.internal.compat;

import io.redspace.ironslib.attribute.AttributeRemapRegistry;
import io.redspace.ironslib.internal.compat.curios.CuriosProxy;
import io.redspace.ironslib.internal.compat.curios.CuriosProxyImpl;
import io.redspace.ironslib.registry.IronsLibRegistries;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public class CompatHandler {
   public static CuriosProxy CURIOS_PROXY = new CuriosProxy() {
   };
   private static final Map<String, Runnable> MOD_MAP = Map.of("curios", () -> CURIOS_PROXY = new CuriosProxyImpl());

   public static void init() {
      MOD_MAP.forEach((modid, supplier) -> {
         if (ModList.get().isLoaded(modid)) {
            supplier.run();
         }
      });
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.ARMOR_PIERCE, ResourceLocation.parse("apothic_attributes:armor_pierce"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.MINING_SPEED, ResourceLocation.parse("apothic_attributes:mining_speed"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.EXPERIENCE_GAINED, ResourceLocation.parse("apothic_attributes:experience_gained"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.ARROW_DAMAGE, ResourceLocation.parse("apothic_attributes:arrow_damage"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.CRIT_DAMAGE, ResourceLocation.parse("apothic_attributes:crit_damage"));
      AttributeRemapRegistry.register(IronsLibRegistries.AttributeRegistry.DODGE_CHANCE, ResourceLocation.parse("apothic_attributes:dodge_chance"));
   }
}
