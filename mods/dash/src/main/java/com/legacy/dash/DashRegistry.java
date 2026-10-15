package com.legacy.dash;

import java.util.function.Supplier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Builder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(
   modid = "dash"
)
public class DashRegistry implements IModBusEvent {
   static final ResourceKey<Enchantment> DASHING_ENCHANTMENT = ResourceKey.create(Registries.ENCHANTMENT, DashMod.locate("dashing"));
   static final Supplier<DataComponentType<?>> DASH_COMPONENT_TYPE = Lazy.of(() -> DataComponentType.<Unit>builder().persistent(Unit.CODEC).build());
   static final ResourceLocation DASH_SOUND_KEY = DashMod.locate("entity.player.ench_dash");
   public static final Supplier<SoundEvent> DASH_SOUND = Lazy.of(() -> SoundEvent.createVariableRangeEvent(DASH_SOUND_KEY));

   @SubscribeEvent
   public static void onRegistry(RegisterEvent event) {
      if (event.getRegistryKey().equals(Registries.SOUND_EVENT)) {
         event.register(Registries.SOUND_EVENT, DASH_SOUND_KEY, DASH_SOUND);
      } else if (event.getRegistryKey().equals(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE)) {
         event.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, DashMod.locate("can_trigger_dashing"), DASH_COMPONENT_TYPE);
      }
   }

   static void enchantmentBootstrap(BootstrapContext<Enchantment> bootstrap) {
      HolderGetter<Item> itemReg = bootstrap.lookup(Registries.ITEM);
      HolderGetter<Enchantment> enchReg = bootstrap.lookup(Registries.ENCHANTMENT);
      registerEnchant(
         bootstrap,
         DASHING_ENCHANTMENT,
         Enchantment.enchantment(
               Enchantment.definition(
                  itemReg.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                  2,
                  1,
                  Enchantment.dynamicCost(10, 10),
                  Enchantment.dynamicCost(25, 10),
                  4,
                  new EquipmentSlotGroup[]{EquipmentSlotGroup.FEET}
               )
            )
            .exclusiveWith(enchReg.getOrThrow(DashEnchantmentTags.DASHING_INCOMPATIBLE))
            .withSpecialEffect((net.minecraft.core.component.DataComponentType) DASH_COMPONENT_TYPE.get(), net.minecraft.util.Unit.INSTANCE)
      );
   }

   static void registerEnchant(BootstrapContext<Enchantment> pContext, ResourceKey<Enchantment> pKey, Builder pBuilder) {
      pContext.register(pKey, pBuilder.build(pKey.location()));
   }
}
