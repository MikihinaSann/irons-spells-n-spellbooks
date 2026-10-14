package com.gametechbc.traveloptics.init;

import com.gametechbc.traveloptics.api.item.HeavyAttributeCurio;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TOAugments {
   private static final DeferredRegister<Item> AUGMENTS = DeferredRegister.create(Registries.ITEM, "traveloptics");
   public static final Supplier<HeavyAttributeCurio> TEST_AUGMENT = AUGMENTS.register(
      "test_augment",
      () -> new HeavyAttributeCurio(
         new Properties().stacksTo(1).rarity(Rarity.RARE),
         Map.of(
            (Attribute)AttributeRegistry.NATURE_SPELL_POWER.get(),
            new AttributeModifier(new ResourceLocation("traveloptics", "nature_spell_power"), -0.35, Operation.ADD_MULTIPLIED_BASE),
            (Attribute)AttributeRegistry.BLOOD_SPELL_POWER.get(),
            new AttributeModifier(new ResourceLocation("traveloptics", "blood_spell_power"), 0.35, Operation.ADD_MULTIPLIED_BASE)
         )
      )
   );

   public TOAugments() {
      super();
   }

   public static void register(IEventBus eventBus) {
      AUGMENTS.register(eventBus);
   }

   public static Collection<DeferredHolder<Item, ? extends Item>> getIronsItems() {
      return AUGMENTS.getEntries();
   }
}
